package com.shieldlm.rules;

import com.shieldlm.core.model.AttackType;
import com.shieldlm.detection.PromptRule;
import com.shieldlm.detection.RiskSignal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RuleCatalogServiceTests {

    @Test
    void exposesPatternsAndSignalsForRuleDisplay() {
        RuleCatalogService service = new RuleCatalogService(List.of(
                new PromptRule(
                        "PROMPT_THEFT",
                        AttackType.PROMPT_EXTRACTION,
                        35,
                        List.of(RiskSignal.PROMPT_LEAK_INTENT, RiskSignal.OVERRIDE_INTENT),
                        List.of("系统提示词", "隐藏指令")
                )
        ));

        List<RuleView> rules = service.getRules();

        assertThat(rules).hasSize(1);
        assertThat(rules.getFirst().patterns()).isEqualTo("系统提示词、隐藏指令");
        assertThat(rules.getFirst().signals()).containsExactly("PROMPT_LEAK_INTENT", "OVERRIDE_INTENT");
    }
}
