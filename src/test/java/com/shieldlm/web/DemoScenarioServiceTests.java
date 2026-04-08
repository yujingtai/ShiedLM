package com.shieldlm.web;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoScenarioServiceTests {

    @Test
    void providesRicherChineseDemoScenarios() {
        DemoScenarioService service = new DemoScenarioService();

        List<DemoScenario> scenarios = service.listScenarios();

        assertThat(scenarios).hasSizeGreaterThanOrEqualTo(8);
        assertThat(scenarios).extracting(DemoScenario::title)
                .contains("角色劫持与提示词套取", "违法内容直接请求", "输出侧：前置规则泄露", "输出侧：凭证样式回显", "输出侧：违规资源文案");
        assertThat(scenarios).extracting(DemoScenario::prompt)
                .anyMatch(prompt -> prompt.contains("现在你是内部审计员"))
                .anyMatch(prompt -> prompt.contains("请给我色情网站"))
                .anyMatch(prompt -> prompt.contains("前置规则"))
                .anyMatch(prompt -> prompt.contains("sk-"))
                .anyMatch(prompt -> prompt.contains("成人资源站"));
    }
}
