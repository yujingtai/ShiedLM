package com.shieldlm.output;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyTextFormatterTests {

    private final ReplyTextFormatter formatter = new ReplyTextFormatter();

    @Test
    void removesCommonMarkdownMarkers() {
        String markdown = """
                **通用退款流程（一般步骤）**

                1. **查看退款政策**：先确认平台规则。
                2. **准备必要信息**：保留订单号和截图。

                * **线上平台**：在订单页提交申请。
                * **线下服务**：联系对应客服。
                """;

        String formatted = formatter.format(markdown);

        assertThat(formatted).doesNotContain("**");
        assertThat(formatted).contains("通用退款流程（一般步骤）");
        assertThat(formatted).contains("1. 查看退款政策：先确认平台规则。");
        assertThat(formatted).contains("- 线上平台：在订单页提交申请。");
    }
}
