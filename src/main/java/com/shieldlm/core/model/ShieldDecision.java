package com.shieldlm.core.model;

import java.util.List;

public record ShieldDecision(
        RiskLevel riskLevel,
        DefenseAction action,
        int totalScore,
        List<DetectionHit> hits,
        String processedPrompt
) {
}
