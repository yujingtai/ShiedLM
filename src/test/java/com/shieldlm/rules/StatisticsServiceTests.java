package com.shieldlm.rules;

import com.shieldlm.audit.AuditRecord;
import com.shieldlm.audit.AuditRecordRepository;
import com.shieldlm.core.model.AttackType;
import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import com.shieldlm.detection.PromptRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTests {

    @Mock
    private AuditRecordRepository auditRecordRepository;

    @Test
    void aggregatesAuditMetricsIntoSummary() {
        StatisticsService statisticsService = new StatisticsService(
                auditRecordRepository,
                List.of(
                        new PromptRule("SYSTEM_PROMPT_LEAK", AttackType.PROMPT_EXTRACTION, 40, List.of("system prompt")),
                        new PromptRule("IGNORE_PREVIOUS_RULES", AttackType.PRIVILEGE_OVERRIDE, 30, List.of("ignore previous"))
                )
        );

        when(auditRecordRepository.count()).thenReturn(12L);
        when(auditRecordRepository.countByRiskLevel(RiskLevel.HIGH)).thenReturn(4L);
        when(auditRecordRepository.countByDefenseAction(DefenseAction.BLOCK)).thenReturn(3L);
        when(auditRecordRepository.countByOutputBlockedTrue()).thenReturn(2L);
        when(auditRecordRepository.findAll()).thenReturn(List.of(
                new AuditRecord(
                        LocalDateTime.of(2026, 4, 7, 10, 0),
                        "p1",
                        "PROMPT_EXTRACTION, PRIVILEGE_OVERRIDE",
                        RiskLevel.HIGH,
                        DefenseAction.BLOCK,
                        false,
                        "r1"
                ),
                new AuditRecord(
                        LocalDateTime.of(2026, 4, 7, 10, 5),
                        "p2",
                        "PROMPT_EXTRACTION",
                        RiskLevel.MEDIUM,
                        DefenseAction.REWRITE,
                        false,
                        "r2"
                )
        ));

        StatisticsSummary summary = statisticsService.getSummary();

        assertThat(summary.totalRequests()).isEqualTo(12);
        assertThat(summary.highRiskRequests()).isEqualTo(4);
        assertThat(summary.blockedRequests()).isEqualTo(3);
        assertThat(summary.outputBlockedRequests()).isEqualTo(2);
        assertThat(summary.attackTypeStats()).hasSize(2);
        assertThat(summary.attackTypeStats().get(0).attackType()).isEqualTo("PROMPT_EXTRACTION");
        assertThat(summary.attackTypeStats().get(0).hitCount()).isEqualTo(2);
        assertThat(summary.attackTypeStats().get(1).attackType()).isEqualTo("PRIVILEGE_OVERRIDE");
        assertThat(summary.attackTypeStats().get(1).hitCount()).isEqualTo(1);
        assertThat(summary.ruleHitStats()).hasSize(2);
        assertThat(summary.ruleHitStats().get(0).ruleKey()).isEqualTo("SYSTEM_PROMPT_LEAK");
        assertThat(summary.ruleHitStats().get(0).hitCount()).isEqualTo(2);
        assertThat(summary.ruleHitStats().get(1).ruleKey()).isEqualTo("IGNORE_PREVIOUS_RULES");
        assertThat(summary.ruleHitStats().get(1).hitCount()).isEqualTo(1);
    }
}
