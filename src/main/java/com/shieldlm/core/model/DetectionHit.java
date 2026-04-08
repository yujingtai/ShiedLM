package com.shieldlm.core.model;

import com.shieldlm.detection.RiskSignal;

import java.util.List;

public record DetectionHit(
        String ruleKey,
        AttackType attackType,
        int score,
        List<RiskSignal> signals
) {

    public DetectionHit(String ruleKey, AttackType attackType, int score) {
        this(ruleKey, attackType, score, List.of());
    }
}
