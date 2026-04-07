# ShieldLM Audit Search And Pagination Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为审计页补充关键词搜索与分页能力，并保持现有筛选与摘要展示可用。

**Architecture:** 本次增强围绕审计页一条链路推进：先扩展控制器测试，再升级 repository/service/controller 查询方式，最后补模板与样式。查询在仓储层完成，控制器负责传递分页状态，模板负责保留筛选条件与翻页入口。

**Tech Stack:** Java 21, Spring Boot, Spring Data JPA, Thymeleaf, JUnit 5, MockMvc

---

### Task 1: 扩展控制器测试

**Files:**
- Modify: `src/test/java/com/shieldlm/audit/AuditControllerTests.java`

- [ ] **Step 1: 写分页与关键词查询的失败测试**

```java
@Test
void searchesAuditPageWithKeywordAndPagination() throws Exception {
    Page<AuditRecord> page = new PageImpl<>(List.of(record), PageRequest.of(1, 5), 7);
    when(auditLogService.findRecent("prompt", RiskLevel.HIGH, DefenseAction.BLOCK, 1)).thenReturn(page);

    mockMvc.perform(get("/audit")
                    .param("keyword", "prompt")
                    .param("riskLevel", "HIGH")
                    .param("defenseAction", "BLOCK")
                    .param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("keyword", "prompt"))
            .andExpect(model().attribute("pageNumber", 1))
            .andExpect(model().attribute("pageNumberDisplay", 2))
            .andExpect(model().attribute("totalPages", 2))
            .andExpect(model().attribute("totalMatches", 7L));

    verify(auditLogService).findRecent("prompt", RiskLevel.HIGH, DefenseAction.BLOCK, 1);
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=AuditControllerTests test`
Expected: FAIL，因为控制器和服务签名尚未支持 `keyword` 与分页模型参数。

### Task 2: 升级后端查询链路

**Files:**
- Modify: `src/main/java/com/shieldlm/audit/AuditRecordRepository.java`
- Modify: `src/main/java/com/shieldlm/audit/AuditLogService.java`
- Modify: `src/main/java/com/shieldlm/audit/AuditController.java`

- [ ] **Step 1: 在 repository 中补组合查询接口**

```java
@Query("""
        select r from AuditRecord r
        where (:riskLevel is null or r.riskLevel = :riskLevel)
          and (:defenseAction is null or r.defenseAction = :defenseAction)
          and (:keyword is null
               or lower(coalesce(r.userPrompt, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(r.attackTypes, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(r.finalReply, '')) like lower(concat('%', :keyword, '%')))
        """)
Page<AuditRecord> search(
        @Param("keyword") String keyword,
        @Param("riskLevel") RiskLevel riskLevel,
        @Param("defenseAction") DefenseAction defenseAction,
        Pageable pageable
);
```

- [ ] **Step 2: 在 service 中补分页查询方法**

```java
public Page<AuditRecord> findRecent(String keyword, RiskLevel riskLevel, DefenseAction defenseAction, int page) {
    String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
    int safePage = Math.max(page, 0);
    Pageable pageable = PageRequest.of(safePage, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
    return auditRecordRepository.search(normalizedKeyword, riskLevel, defenseAction, pageable);
}
```

- [ ] **Step 3: 在 controller 中改为分页模型**

```java
Page<AuditRecord> recordPage = auditLogService.findRecent(keyword, riskLevel, defenseAction, page);
List<AuditRecord> records = recordPage.getContent();

model.addAttribute("records", records);
model.addAttribute("keyword", keyword);
model.addAttribute("pageNumber", recordPage.getNumber());
model.addAttribute("pageNumberDisplay", recordPage.getTotalPages() == 0 ? 0 : recordPage.getNumber() + 1);
model.addAttribute("totalPages", recordPage.getTotalPages());
model.addAttribute("totalMatches", recordPage.getTotalElements());
model.addAttribute("hasPrevious", recordPage.hasPrevious());
model.addAttribute("hasNext", recordPage.hasNext());
```

- [ ] **Step 4: 运行控制器测试确认通过**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=AuditControllerTests test`
Expected: PASS

### Task 3: 更新审计页模板

**Files:**
- Modify: `src/main/resources/templates/audit.html`
- Modify: `src/main/resources/static/css/app.css`

- [ ] **Step 1: 在筛选表单中加入关键词输入框**

```html
<div class="filter-field filter-field-wide">
    <label for="keyword">关键词检索</label>
    <input id="keyword" name="keyword" type="text" th:value="${keyword}" placeholder="搜索提示词、攻击类型或最终回复">
</div>
```

- [ ] **Step 2: 在摘要区和表格下方加入分页信息**

```html
<span class="summary-note"
      th:text="${'共匹配 ' + totalMatches + ' 条，当前第 ' + pageNumberDisplay + ' / ' + totalPages + ' 页'}">
    共匹配 0 条，当前第 0 / 0 页
</span>
```

```html
<nav class="pagination-bar" th:if="${totalPages > 1}">
    <a th:if="${hasPrevious}"
       th:href="@{/audit(page=${pageNumber - 1}, keyword=${keyword}, riskLevel=${selectedRiskLevel}, defenseAction=${selectedDefenseAction})}">
        上一页
    </a>
    <span th:if="${!hasPrevious}" class="is-disabled">上一页</span>
    <span class="pagination-status" th:text="${'第 ' + pageNumberDisplay + ' / ' + totalPages + ' 页'}">第 1 / 1 页</span>
    <a th:if="${hasNext}"
       th:href="@{/audit(page=${pageNumber + 1}, keyword=${keyword}, riskLevel=${selectedRiskLevel}, defenseAction=${selectedDefenseAction})}">
        下一页
    </a>
    <span th:if="${!hasNext}" class="is-disabled">下一页</span>
</nav>
```

- [ ] **Step 3: 补模板样式**

```css
.filter-field input {
    width: 100%;
    border: 1px solid var(--line);
    border-radius: 14px;
    padding: 12px 14px;
    font: inherit;
    background: #fff;
}

.filter-field-wide {
    grid-column: span 2;
}

.pagination-bar {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 18px;
}
```

- [ ] **Step 4: 跑全量测试确认无回归**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository test`
Expected: PASS with 0 failures

### Task 4: 提交本批次改动

**Files:**
- Modify: `progress.md`
- Modify: `findings.md`

- [ ] **Step 1: 更新进度记录**

```text
记录审计页关键词搜索与分页的实现状态、测试命令和结果。
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/shieldlm/audit src/main/resources/templates/audit.html src/main/resources/static/css/app.css src/test/java/com/shieldlm/audit/AuditControllerTests.java progress.md findings.md docs/plans/2026-04-07-audit-search-pagination-implementation.md
git commit -m "feat: add audit search and pagination"
```
