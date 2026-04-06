package com.shieldlm.core.model;

public record DetectionHit(String ruleKey, AttackType attackType, int score) {
}
