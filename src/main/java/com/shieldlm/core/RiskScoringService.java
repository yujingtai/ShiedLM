package com.shieldlm.core;

import com.shieldlm.core.model.DetectionHit;

import java.util.List;

public class RiskScoringService {

    /**
     * 把所有命中规则的分值直接累加，得到本轮输入的总风险分。
     * 当前评分方式保持直观和可解释性，可直接反映命中结果与风险等级之间的关系。
     */
    public int score(List<DetectionHit> hits) {
        return hits.stream()
                .mapToInt(DetectionHit::score)
                .sum();
    }
}
