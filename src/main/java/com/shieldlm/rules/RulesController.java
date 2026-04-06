package com.shieldlm.rules;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RulesController {

    private final RuleCatalogService ruleCatalogService;
    private final StatisticsService statisticsService;

    public RulesController(RuleCatalogService ruleCatalogService, StatisticsService statisticsService) {
        this.ruleCatalogService = ruleCatalogService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/rules")
    public String rules(Model model) {
        model.addAttribute("rules", ruleCatalogService.getRules());
        model.addAttribute("stats", statisticsService.getSummary());
        return "rules";
    }
}
