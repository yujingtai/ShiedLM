package com.shieldlm.rules;

import com.shieldlm.audit.AuditRecord;
import com.shieldlm.audit.AuditRecordRepository;
import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import com.shieldlm.detection.PromptRule;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final AuditRecordRepository auditRecordRepository;
    private final List<PromptRule> promptRules;

    public StatisticsService(AuditRecordRepository auditRecordRepository, List<PromptRule> promptRules) {
        this.auditRecordRepository = auditRecordRepository;
        this.promptRules = promptRules;
    }

    /**
     * 汇总规则页需要的基础指标与分析结果，避免模板处理拆分、排序等逻辑。
     */
    public StatisticsSummary getSummary() {
        Map<String, Long> attackTypeCountMap = buildAttackTypeCountMap();

        return new StatisticsSummary(
                auditRecordRepository.count(),
                auditRecordRepository.countByRiskLevel(RiskLevel.HIGH),
                auditRecordRepository.countByDefenseAction(DefenseAction.BLOCK),
                auditRecordRepository.countByOutputBlockedTrue(),
                buildAttackTypeStats(attackTypeCountMap),
                buildRuleHitStats(attackTypeCountMap)
        );
    }

    private Map<String, Long> buildAttackTypeCountMap() {
        return auditRecordRepository.findAll().stream()
                .map(AuditRecord::getAttackTypes)
                .filter(Objects::nonNull)
                .flatMap(value -> Arrays.stream(value.split(",")))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.groupingBy(value -> value, Collectors.counting()));
    }

    private List<AttackTypeStatView> buildAttackTypeStats(Map<String, Long> attackTypeCountMap) {
        return attackTypeCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .map(entry -> new AttackTypeStatView(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<RuleHitStatView> buildRuleHitStats(Map<String, Long> attackTypeCountMap) {
        return promptRules.stream()
                .map(rule -> new RuleHitStatView(
                        rule.key(),
                        rule.attackType().name(),
                        attackTypeCountMap.getOrDefault(rule.attackType().name(), 0L)
                ))
                .sorted(Comparator.comparingLong(RuleHitStatView::hitCount).reversed()
                        .thenComparing(RuleHitStatView::ruleKey))
                .toList();
    }
}
