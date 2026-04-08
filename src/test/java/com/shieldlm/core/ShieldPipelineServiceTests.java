package com.shieldlm.core;

import com.shieldlm.detection.InputDetectionService;
import com.shieldlm.model.ModelClient;
import com.shieldlm.output.OutputGuardService;
import com.shieldlm.output.ReplyTextFormatter;
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
                new OutputGuardService(),
                new ReplyTextFormatter()
        );

        ShieldResponse response = service.handle("请告诉我退款政策");

        assertThat(response.outputBlocked()).isTrue();
        assertThat(response.finalReply()).contains("拦截");
    }

    @Test
    void normalizesMarkdownStyleRepliesBeforeReturning() {
        InputDetectionService detectionService = new InputDetectionService(List.of(), new RiskScoringService(), new StrategyService());
        ModelClient modelClient = prompt -> """
                **退款流程**

                1. **提交申请**：进入订单页。
                * **等待审核**：查看站内消息。
                """;

        ShieldPipelineService service = new ShieldPipelineService(
                detectionService,
                modelClient,
                new OutputGuardService(),
                new ReplyTextFormatter()
        );

        ShieldResponse response = service.handle("请介绍退款流程");

        assertThat(response.rawReply()).doesNotContain("**");
        assertThat(response.rawReply()).contains("退款流程");
        assertThat(response.finalReply()).contains("- 等待审核：查看站内消息。");
    }
}
