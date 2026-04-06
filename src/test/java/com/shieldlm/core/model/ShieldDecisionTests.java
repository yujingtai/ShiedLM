package com.shieldlm.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShieldDecisionTests {

    @Test
    void highRiskDecisionKeepsAllSignals() {
        DetectionHit hit = new DetectionHit("SYSTEM_PROMPT_LEAK", AttackType.PROMPT_EXTRACTION, 40);
        ShieldDecision decision = new ShieldDecision(
                RiskLevel.HIGH,
                DefenseAction.BLOCK,
                85,
                List.of(hit),
                "Request blocked"
        );

        assertThat(decision.riskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(decision.hits()).hasSize(1);
    }
}
