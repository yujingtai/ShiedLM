package com.shieldlm.core;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.DetectionHit;
import com.shieldlm.core.model.RiskLevel;
import com.shieldlm.core.model.ShieldDecision;

import java.util.List;
import java.util.regex.Pattern;

public class StrategyService {

    private static final Pattern IGNORE_PREVIOUS_PATTERN = Pattern.compile("(?i)ignore previous[^。？！!?\\n]*");

    /**
     * 这里负责把“分数”翻译成真正的系统动作。
     * processedPrompt 表示经过策略处理后，最终允许送给模型的提示词。
     */
    public ShieldDecision decide(String prompt, List<DetectionHit> hits, int totalScore) {
        if (totalScore >= 70) {
            return new ShieldDecision(RiskLevel.HIGH, DefenseAction.BLOCK, totalScore, hits, "Request blocked");
        }
        if (totalScore >= 30) {
            return new ShieldDecision(
                    RiskLevel.MEDIUM,
                    DefenseAction.REWRITE,
                    totalScore,
                    hits,
                    IGNORE_PREVIOUS_PATTERN.matcher(prompt).replaceAll("").trim()
            );
        }
        return new ShieldDecision(RiskLevel.LOW, DefenseAction.ALLOW, totalScore, hits, prompt);
    }
}
