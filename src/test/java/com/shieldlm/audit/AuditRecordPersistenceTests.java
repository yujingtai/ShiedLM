package com.shieldlm.audit;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuditRecordPersistenceTests {

    @Autowired
    private AuditRecordRepository auditRecordRepository;

    @Test
    void savesLongFinalReply() {
        String longReply = "安全回复片段。".repeat(200);

        AuditRecord saved = auditRecordRepository.save(new AuditRecord(
                LocalDateTime.now(),
                "请介绍退款流程",
                "PROMPT_EXTRACTION",
                "PROMPT_LEAK_INTENT",
                RiskLevel.LOW,
                DefenseAction.ALLOW,
                false,
                "NONE",
                longReply
        ));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRiskSignals()).isEqualTo("PROMPT_LEAK_INTENT");
        assertThat(saved.getOutputRiskType()).isEqualTo("NONE");
        assertThat(saved.getFinalReply()).hasSize(longReply.length());
    }
}
