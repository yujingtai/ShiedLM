package com.shieldlm.rules;

import com.shieldlm.core.model.AttackType;

import java.util.List;

public record RuleView(
        String key,
        AttackType attackType,
        int score,
        String patterns,
        List<String> signals
) {
}
