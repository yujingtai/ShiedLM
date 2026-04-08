package com.shieldlm.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ViewTextHelperTests {

    private final ViewTextHelper viewTextHelper = new ViewTextHelper();

    @Test
    void mapsSemanticRuleKeyToChineseLabel() {
        assertThat(viewTextHelper.ruleKey("SEMANTIC_ILLEGAL_HARMFUL_CONTENT"))
                .isEqualTo("语义裁决：违法危险内容");
    }
}
