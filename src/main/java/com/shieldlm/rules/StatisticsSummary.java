package com.shieldlm.rules;

import java.util.List;

public record StatisticsSummary(
        long totalRequests,
        long highRiskRequests,
        long blockedRequests,
        long outputBlockedRequests,
        List<AttackTypeStatView> attackTypeStats,
        List<RuleHitStatView> ruleHitStats
) {
}
