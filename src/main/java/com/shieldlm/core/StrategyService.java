package com.shieldlm.core;

import com.shieldlm.core.model.AttackType;
import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.DetectionHit;
import com.shieldlm.core.model.RiskLevel;
import com.shieldlm.core.model.ShieldDecision;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class StrategyService {

    private static final Set<AttackType> HARD_BLOCK_TYPES = EnumSet.of(
            AttackType.PROMPT_EXTRACTION,
            AttackType.SENSITIVE_DATA_INDUCTION,
            AttackType.POLICY_EVASION,
            AttackType.ILLEGAL_HARMFUL_CONTENT
    );

    private static final Pattern IGNORE_PREVIOUS_PATTERN = Pattern.compile("(?i)ignore previous[^,，。！？\\n]*");
    private static final Pattern CHINESE_OVERRIDE_PATTERN = Pattern.compile(
            "忽略之前[^,，。！？\\n]*|不要遵守前面的限制[^,，。！？\\n]*|绕过限制[^,，。！？\\n]*|无视规则[^,，。！？\\n]*|跳过限制[^,，。！？\\n]*"
    );
    private static final Pattern ROLE_PREFIX_PATTERN = Pattern.compile(
            "现在你是[^,，。！？\\n]*|请你扮演[^,，。！？\\n]*|你现在作为[^,，。！？\\n]*|切换为[^,，。！？\\n]*"
    );
    private static final Pattern LEADING_CONNECTOR_PATTERN = Pattern.compile("^[,，。！？\\s]*(然后|并且|并|再|接着)?[,，。！？\\s]*");
    private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s+");

    public ShieldDecision decide(String prompt, List<DetectionHit> hits, int totalScore) {
        if (shouldHardBlock(hits, totalScore)) {
            return new ShieldDecision(RiskLevel.HIGH, DefenseAction.BLOCK, totalScore, hits, "请求因高风险被系统拦截。");
        }
        if (totalScore >= 30) {
            String rewrittenPrompt = rewritePrompt(prompt);
            return new ShieldDecision(
                    RiskLevel.MEDIUM,
                    DefenseAction.REWRITE,
                    totalScore,
                    hits,
                    rewrittenPrompt
            );
        }
        return new ShieldDecision(RiskLevel.LOW, DefenseAction.ALLOW, totalScore, hits, prompt);
    }

    private boolean shouldHardBlock(List<DetectionHit> hits, int totalScore) {
        if (totalScore >= 70) {
            return true;
        }
        return hits.stream()
                .map(DetectionHit::attackType)
                .anyMatch(HARD_BLOCK_TYPES::contains);
    }

    private String rewritePrompt(String prompt) {
        String rewrittenPrompt = prompt == null ? "" : prompt;
        rewrittenPrompt = IGNORE_PREVIOUS_PATTERN.matcher(rewrittenPrompt).replaceAll("");
        rewrittenPrompt = CHINESE_OVERRIDE_PATTERN.matcher(rewrittenPrompt).replaceAll("");
        rewrittenPrompt = ROLE_PREFIX_PATTERN.matcher(rewrittenPrompt).replaceAll("");
        rewrittenPrompt = LEADING_CONNECTOR_PATTERN.matcher(rewrittenPrompt).replaceFirst("");
        rewrittenPrompt = MULTI_SPACE_PATTERN.matcher(rewrittenPrompt).replaceAll(" ").trim();
        return rewrittenPrompt;
    }
}
