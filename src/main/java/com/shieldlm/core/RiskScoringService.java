package com.shieldlm.core;

import com.shieldlm.core.model.DetectionHit;

import java.util.List;

public class RiskScoringService {

    public int score(List<DetectionHit> hits) {
        int totalScore = hits.stream()
                .mapToInt(DetectionHit::score)
                .sum();

        long distinctAttackTypes = hits.stream()
                .map(DetectionHit::attackType)
                .distinct()
                .count();
        if (distinctAttackTypes >= 2) {
            totalScore += 15;
        }
        if (hits.size() >= 3) {
            totalScore += 10;
        }
        return Math.min(totalScore, 100);
    }
}
