package com.shieldlm.rules;

import com.shieldlm.core.model.AttackType;

public record RuleView(
        String key,
        AttackType attackType,
        int score,
        String patterns
) {
}
