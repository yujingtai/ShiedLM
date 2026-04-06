package com.shieldlm.core;

import com.shieldlm.core.model.DetectionHit;

import java.util.List;

public class RiskScoringService {

    /**
     * 把所有命中规则的分值直接累加，得到本轮输入的总风险分。
     * 先保持算法足够直观，方便在论文和答辩里解释评分依据。
     */
    public int score(List<DetectionHit> hits) {
        return hits.stream()
                .mapToInt(DetectionHit::score)
                .sum();
    }
}
