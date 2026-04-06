package com.shieldlm.core;

import com.shieldlm.core.model.DetectionHit;

import java.util.List;

public class RiskScoringService {

    public int score(List<DetectionHit> hits) {
        return hits.stream()
                .mapToInt(DetectionHit::score)
                .sum();
    }
}
