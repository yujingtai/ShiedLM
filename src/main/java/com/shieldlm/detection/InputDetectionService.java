package com.shieldlm.detection;

import com.shieldlm.core.RiskScoringService;
import com.shieldlm.core.StrategyService;
import com.shieldlm.core.model.DetectionHit;
import com.shieldlm.core.model.ShieldDecision;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InputDetectionService {

    private final List<PromptRule> rules;
    private final RiskScoringService riskScoringService;
    private final StrategyService strategyService;

    public InputDetectionService(
            List<PromptRule> rules,
            RiskScoringService riskScoringService,
            StrategyService strategyService
    ) {
        this.rules = rules;
        this.riskScoringService = riskScoringService;
        this.strategyService = strategyService;
    }

    public ShieldDecision analyze(String prompt) {
        // 统一转成小写，避免规则匹配被大小写差异干扰。
        String normalizedPrompt = prompt.toLowerCase(Locale.ROOT);
        List<DetectionHit> hits = new ArrayList<>();

        for (PromptRule rule : rules) {
            boolean matched = rule.patterns().stream()
                    .map(pattern -> pattern.toLowerCase(Locale.ROOT))
                    .anyMatch(normalizedPrompt::contains);
            if (matched) {
                // 这里只记录命中事实，真正的风险等级和系统动作交给后面的服务处理。
                hits.add(new DetectionHit(rule.key(), rule.attackType(), rule.score()));
            }
        }

        int totalScore = riskScoringService.score(hits);
        return strategyService.decide(prompt, hits, totalScore);
    }
}
