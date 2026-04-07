# ShieldLM Rules Analytics Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为规则页补充攻击类型分布和规则命中排行，让页面同时具备规则定义展示与运行反馈展示。

**Architecture:** 本次增强围绕 `StatisticsService` 展开，先扩展统计汇总对象，再把审计记录中的攻击类型拆分统计成分布结果，并根据规则目录映射出规则命中排行。`RulesController` 继续透传统一的 `stats` 模型，`rules.html` 只负责展示新的分析区块。

**Tech Stack:** Java 21, Spring Boot, Spring Data JPA, Thymeleaf, JUnit 5, Mockito, MockMvc

---

### Task 1: 扩展统计服务测试

**Files:**
- Modify: `src/test/java/com/shieldlm/rules/StatisticsServiceTests.java`

- [ ] **Step 1: 写攻击类型分布与规则命中排行的失败测试**

```java
@Test
void aggregatesAttackTypeDistributionAndRuleHits() {
    List<AuditRecord> records = List.of(
            new AuditRecord(LocalDateTime.of(2026, 4, 7, 10, 0), "p1", "PROMPT_EXTRACTION, PRIVILEGE_OVERRIDE", RiskLevel.HIGH, DefenseAction.BLOCK, false, "r1"),
            new AuditRecord(LocalDateTime.of(2026, 4, 7, 10, 5), "p2", "PROMPT_EXTRACTION", RiskLevel.MEDIUM, DefenseAction.REWRITE, false, "r2")
    );
    List<PromptRule> rules = List.of(
            new PromptRule("SYSTEM_PROMPT_LEAK", AttackType.PROMPT_EXTRACTION, 40, List.of("system prompt")),
            new PromptRule("IGNORE_PREVIOUS_RULES", AttackType.PRIVILEGE_OVERRIDE, 30, List.of("ignore previous"))
    );

    when(auditRecordRepository.count()).thenReturn(12L);
    when(auditRecordRepository.countByRiskLevel(RiskLevel.HIGH)).thenReturn(4L);
    when(auditRecordRepository.countByDefenseAction(DefenseAction.BLOCK)).thenReturn(3L);
    when(auditRecordRepository.countByOutputBlockedTrue()).thenReturn(2L);
    when(auditRecordRepository.findAll()).thenReturn(records);

    StatisticsService service = new StatisticsService(auditRecordRepository, rules);
    StatisticsSummary summary = service.getSummary();

    assertThat(summary.attackTypeStats()).hasSize(2);
    assertThat(summary.attackTypeStats().get(0).attackType()).isEqualTo("PROMPT_EXTRACTION");
    assertThat(summary.attackTypeStats().get(0).hitCount()).isEqualTo(2);
    assertThat(summary.ruleHitStats()).hasSize(2);
    assertThat(summary.ruleHitStats().get(0).ruleKey()).isEqualTo("SYSTEM_PROMPT_LEAK");
    assertThat(summary.ruleHitStats().get(0).hitCount()).isEqualTo(2);
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=StatisticsServiceTests test`
Expected: FAIL，因为 `StatisticsSummary` 还没有新字段，`StatisticsService` 也还没有聚合逻辑。

### Task 2: 实现统计聚合模型

**Files:**
- Create: `src/main/java/com/shieldlm/rules/AttackTypeStatView.java`
- Create: `src/main/java/com/shieldlm/rules/RuleHitStatView.java`
- Modify: `src/main/java/com/shieldlm/rules/StatisticsSummary.java`
- Modify: `src/main/java/com/shieldlm/rules/StatisticsService.java`

- [ ] **Step 1: 新增展示对象**

```java
package com.shieldlm.rules;

public record AttackTypeStatView(String attackType, long hitCount) {
}
```

```java
package com.shieldlm.rules;

public record RuleHitStatView(String ruleKey, String attackType, long hitCount) {
}
```

- [ ] **Step 2: 扩展统计汇总对象**

```java
public record StatisticsSummary(
        long totalRequests,
        long highRiskRequests,
        long blockedRequests,
        long outputBlockedRequests,
        List<AttackTypeStatView> attackTypeStats,
        List<RuleHitStatView> ruleHitStats
) {
}
```

- [ ] **Step 3: 在统计服务中补充聚合逻辑**

```java
List<String> attackTypes = auditRecordRepository.findAll().stream()
        .map(AuditRecord::getAttackTypes)
        .filter(Objects::nonNull)
        .flatMap(value -> Arrays.stream(value.split(",")))
        .map(String::trim)
        .filter(value -> !value.isEmpty())
        .toList();

Map<String, Long> attackTypeCountMap = attackTypes.stream()
        .collect(Collectors.groupingBy(value -> value, Collectors.counting()));
```

```java
List<AttackTypeStatView> attackTypeStats = attackTypeCountMap.entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .map(entry -> new AttackTypeStatView(entry.getKey(), entry.getValue()))
        .toList();

List<RuleHitStatView> ruleHitStats = promptRules.stream()
        .map(rule -> new RuleHitStatView(
                rule.key(),
                rule.attackType().name(),
                attackTypeCountMap.getOrDefault(rule.attackType().name(), 0L)
        ))
        .sorted(Comparator.comparingLong(RuleHitStatView::hitCount).reversed()
                .thenComparing(RuleHitStatView::ruleKey))
        .toList();
```

- [ ] **Step 4: 运行统计服务测试确认通过**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=StatisticsServiceTests test`
Expected: PASS

### Task 3: 更新规则页控制器测试与模板

**Files:**
- Modify: `src/test/java/com/shieldlm/rules/RulesControllerTests.java`
- Modify: `src/main/resources/templates/rules.html`
- Modify: `src/main/resources/static/css/app.css`

- [ ] **Step 1: 扩展规则页控制器测试数据**

```java
StatisticsSummary stats = new StatisticsSummary(
        5,
        2,
        1,
        1,
        List.of(new AttackTypeStatView("PROMPT_EXTRACTION", 3)),
        List.of(new RuleHitStatView("SYSTEM_PROMPT_LEAK", "PROMPT_EXTRACTION", 3))
);
```

- [ ] **Step 2: 在规则页模板中新增两个区块**

```html
<section class="panel split-panel">
    <div class="panel-header">
        <h2>攻击类型分布</h2>
        <p>基于审计记录统计近期命中的攻击类型。</p>
    </div>
    <div class="stats-list" th:if="${!#lists.isEmpty(stats.attackTypeStats)}">
        <article class="stats-item" th:each="item : ${stats.attackTypeStats}">
            <strong th:text="${item.attackType}">PROMPT_EXTRACTION</strong>
            <span th:text="${item.hitCount + ' 次'}">0 次</span>
        </article>
    </div>
</section>
```

```html
<section class="panel split-panel">
    <div class="panel-header">
        <h2>规则命中排行</h2>
        <p>用于观察哪些规则对应的攻击类型在近期更常出现。</p>
    </div>
    <div class="table-shell">
        <table class="audit-table">
            <thead>
            <tr>
                <th>规则键</th>
                <th>攻击类型</th>
                <th>命中次数</th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="item : ${stats.ruleHitStats}">
                <td th:text="${item.ruleKey}">SYSTEM_PROMPT_LEAK</td>
                <td th:text="${item.attackType}">PROMPT_EXTRACTION</td>
                <td th:text="${item.hitCount}">3</td>
            </tr>
            </tbody>
        </table>
    </div>
</section>
```

- [ ] **Step 3: 补充页面样式**

```css
.stats-list {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 14px;
}

.stats-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 18px;
    border: 1px solid rgba(217, 207, 192, 0.9);
    border-radius: 18px;
    background: rgba(255, 255, 255, 0.72);
}
```

- [ ] **Step 4: 运行规则页相关测试**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=RulesControllerTests,StatisticsServiceTests test`
Expected: PASS

### Task 4: 完成验证并提交

**Files:**
- Modify: `progress.md`
- Modify: `findings.md`

- [ ] **Step 1: 更新进度与结论**

```text
记录规则页统计增强的实现范围、测试命令和结果。
```

- [ ] **Step 2: 跑全量测试**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository test`
Expected: PASS with 0 failures

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/shieldlm/rules src/main/resources/templates/rules.html src/main/resources/static/css/app.css src/test/java/com/shieldlm/rules progress.md findings.md docs/specs/2026-04-07-rules-analytics-design.md docs/plans/2026-04-07-rules-analytics-implementation.md
git commit -m "feat: enrich rules analytics"
```
