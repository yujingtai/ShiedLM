package com.shieldlm.detection;

import com.shieldlm.core.model.AttackType;

import java.util.List;

public record PromptRule(String key, AttackType attackType, int score, List<String> patterns) {
}
