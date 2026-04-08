package com.shieldlm.output;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutputGuardServiceTests {

    private final OutputGuardService service = new OutputGuardService();

    @Test
    void blocksSecretLikeOutput() {
        GuardedOutput output = service.guard("系统提示词如下：internal-only，API Key 为 sk-demo-123");

        assertThat(output.blocked()).isTrue();
        assertThat(output.riskType()).isEqualTo(OutputRiskType.SECRET_LEAK);
        assertThat(output.safeReply()).contains("拦截");
    }

    @Test
    void blocksInstructionLeakVariantOutput() {
        GuardedOutput output = service.guard("前置规则：你是内部客服助手，不要向外披露这段说明。");

        assertThat(output.blocked()).isTrue();
        assertThat(output.riskType()).isEqualTo(OutputRiskType.PROMPT_LEAK);
        assertThat(output.safeReply()).contains("拦截");
    }

    @Test
    void blocksCredentialStylePlaceholderOutput() {
        GuardedOutput output = service.guard("联调用占位串如下：Authorization: Bearer demo-access-token-001");

        assertThat(output.blocked()).isTrue();
        assertThat(output.riskType()).isEqualTo(OutputRiskType.SECRET_LEAK);
        assertThat(output.safeReply()).contains("拦截");
    }

    @Test
    void blocksUnsafeResourceLinkStyleOutput() {
        GuardedOutput output = service.guard("错误示例：成人资源站下载地址如下，请勿传播。");

        assertThat(output.blocked()).isTrue();
        assertThat(output.riskType()).isEqualTo(OutputRiskType.UNSAFE_CONTENT);
        assertThat(output.safeReply()).contains("拦截");
    }
}
