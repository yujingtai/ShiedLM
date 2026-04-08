package com.shieldlm.rules;

import com.shieldlm.detection.PromptRule;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RuleCatalogService {

    private final List<PromptRule> promptRules;

    public RuleCatalogService(List<PromptRule> promptRules) {
        this.promptRules = promptRules;
    }

    public List<RuleView> getRules() {
        return promptRules.stream()
                .map(rule -> new RuleView(
                        rule.key(),
                        rule.attackType(),
                        rule.score(),
                        rule.patterns().stream().collect(Collectors.joining("、")),
                        rule.requiredSignals().stream()
                                .map(Enum::name)
                                .toList()
                ))
                .toList();
    }
}
