package com.shieldlm.rules;

import com.shieldlm.core.model.AttackType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(RulesController.class)
class RulesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RuleCatalogService ruleCatalogService;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void rendersRulesPageWithRulesAndStats() throws Exception {
        List<RuleView> rules = List.of(
                new RuleView("SYSTEM_PROMPT_LEAK", AttackType.PROMPT_EXTRACTION, 40, "系统提示词、system prompt")
        );
        StatisticsSummary stats = new StatisticsSummary(5, 2, 1, 1);

        when(ruleCatalogService.getRules()).thenReturn(rules);
        when(statisticsService.getSummary()).thenReturn(stats);

        mockMvc.perform(get("/rules"))
                .andExpect(status().isOk())
                .andExpect(view().name("rules"))
                .andExpect(model().attribute("rules", rules))
                .andExpect(model().attribute("stats", stats));
    }
}
