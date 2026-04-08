package com.shieldlm.detection;

import com.shieldlm.core.RiskScoringService;
import com.shieldlm.core.StrategyService;
import com.shieldlm.core.model.DetectionHit;
import com.shieldlm.core.model.ShieldDecision;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InputDetectionService {

    private final List<PromptRule> rules;
    private final PromptNormalizer promptNormalizer;
    private final RiskSignalExtractor riskSignalExtractor;
    private final RiskScoringService riskScoringService;
    private final StrategyService strategyService;
    private final SemanticSafetyJudge semanticSafetyJudge;

    public InputDetectionService(
            List<PromptRule> rules,
            PromptNormalizer promptNormalizer,
            RiskSignalExtractor riskSignalExtractor,
            RiskScoringService riskScoringService,
            StrategyService strategyService
    ) {
        this(
                rules,
                promptNormalizer,
                riskSignalExtractor,
                riskScoringService,
                strategyService,
                SemanticSafetyJudge.noop()
        );
    }

    public InputDetectionService(
            List<PromptRule> rules,
            PromptNormalizer promptNormalizer,
            RiskSignalExtractor riskSignalExtractor,
            RiskScoringService riskScoringService,
            StrategyService strategyService,
            SemanticSafetyJudge semanticSafetyJudge
    ) {
        this.rules = rules;
        this.promptNormalizer = promptNormalizer;
        this.riskSignalExtractor = riskSignalExtractor;
        this.riskScoringService = riskScoringService;
        this.strategyService = strategyService;
        this.semanticSafetyJudge = semanticSafetyJudge;
    }

    public InputDetectionService(
            List<PromptRule> rules,
            RiskScoringService riskScoringService,
            StrategyService strategyService
    ) {
        this(
                rules,
                new PromptNormalizer(),
                new RiskSignalExtractor(),
                riskScoringService,
                strategyService,
                SemanticSafetyJudge.noop()
        );
    }

    public ShieldDecision analyze(String prompt) {
        NormalizedPrompt normalizedPrompt = promptNormalizer.normalize(prompt);
        Set<RiskSignal> extractedSignals = riskSignalExtractor.extract(normalizedPrompt);
        List<DetectionHit> hits = new ArrayList<>();

        for (PromptRule rule : rules) {
            boolean signalMatched = extractedSignals.containsAll(rule.requiredSignals());
            boolean textMatched = rule.patterns().isEmpty()
                    || rule.patterns().stream()
                    .map(pattern -> pattern.toLowerCase(Locale.ROOT).replace(" ", ""))
                    .anyMatch(normalizedPrompt.compactText()::contains);
            if (signalMatched && textMatched) {
                hits.add(new DetectionHit(rule.key(), rule.attackType(), rule.score(), rule.requiredSignals()));
            }
        }

        int totalScore = riskScoringService.score(hits);
        if (shouldRunSemanticJudge(hits, totalScore)) {
            SemanticSafetyVerdict verdict = semanticSafetyJudge.judge(prompt);
            if (verdict.matched()) {
                hits.add(new DetectionHit(
                        verdict.ruleKey(),
                        verdict.attackType(),
                        verdict.score(),
                        verdict.signals()
                ));
                totalScore = riskScoringService.score(hits);
            }
        }
        return strategyService.decide(prompt, hits, totalScore);
    }

    private boolean shouldRunSemanticJudge(List<DetectionHit> hits, int totalScore) {
        return hits.isEmpty() || totalScore < 30;
    }
}
