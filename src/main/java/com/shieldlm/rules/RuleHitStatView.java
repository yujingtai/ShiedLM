package com.shieldlm.rules;

public record RuleHitStatView(
        String ruleKey,
        String attackType,
        long hitCount
) {
}
