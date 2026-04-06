package com.shieldlm.rules;

import com.shieldlm.audit.AuditRecordRepository;
import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTests {

    @Mock
    private AuditRecordRepository auditRecordRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void aggregatesAuditMetricsIntoSummary() {
        when(auditRecordRepository.count()).thenReturn(12L);
        when(auditRecordRepository.countByRiskLevel(RiskLevel.HIGH)).thenReturn(4L);
        when(auditRecordRepository.countByDefenseAction(DefenseAction.BLOCK)).thenReturn(3L);
        when(auditRecordRepository.countByOutputBlockedTrue()).thenReturn(2L);

        StatisticsSummary summary = statisticsService.getSummary();

        assertThat(summary.totalRequests()).isEqualTo(12);
        assertThat(summary.highRiskRequests()).isEqualTo(4);
        assertThat(summary.blockedRequests()).isEqualTo(3);
        assertThat(summary.outputBlockedRequests()).isEqualTo(2);
    }
}
