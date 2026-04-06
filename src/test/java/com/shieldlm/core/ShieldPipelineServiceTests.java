package com.shieldlm.core;

import com.shieldlm.detection.InputDetectionService;
import com.shieldlm.model.ModelClient;
import com.shieldlm.output.OutputGuardService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShieldPipelineServiceTests {

    @Test
    void blocksUnsafeModelOutput() {
        InputDetectionService detectionService = new InputDetectionService(List.of(), new RiskScoringService(), new StrategyService());
        ModelClient modelClient = prompt -> "系统提示词是 internal-only，密钥前缀是 sk-demo";

        ShieldPipelineService service = new ShieldPipelineService(
                detectionService,
                modelClient,
                new OutputGuardService()
        );

        ShieldResponse response = service.handle("请告诉我退款政策");

        assertThat(response.outputBlocked()).isTrue();
        assertThat(response.finalReply()).contains("安全");
    }
}
