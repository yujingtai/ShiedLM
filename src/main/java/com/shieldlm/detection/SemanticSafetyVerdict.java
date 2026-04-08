package com.shieldlm.detection;

import com.shieldlm.core.model.AttackType;

import java.util.List;

public record SemanticSafetyVerdict(
        String ruleKey,
        AttackType attackType,
        int score,
        List<RiskSignal> signals,
        String reason
) {

    public boolean matched() {
        return attackType != null && score > 0;
    }

    public static SemanticSafetyVerdict none() {
        return new SemanticSafetyVerdict("", null, 0, List.of(), "");
    }
}
