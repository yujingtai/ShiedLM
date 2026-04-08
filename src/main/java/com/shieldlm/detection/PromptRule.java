package com.shieldlm.detection;

import com.shieldlm.core.model.AttackType;

import java.util.List;

public record PromptRule(
        String key,
        AttackType attackType,
        int score,
        List<RiskSignal> requiredSignals,
        List<String> patterns
) {

    public PromptRule(String key, AttackType attackType, int score, List<String> patterns) {
        this(key, attackType, score, List.of(), patterns);
    }
}
