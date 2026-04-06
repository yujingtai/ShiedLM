package com.shieldlm.rules;

public record StatisticsSummary(
        long totalRequests,
        long highRiskRequests,
        long blockedRequests,
        long outputBlockedRequests
) {
}
