# ShieldLM 回复文本归一化 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将真实模型返回的 Markdown 风格回复统一转换为适合当前页面展示的纯文本格式。

**Architecture:** 在输出链路中新增一个轻量回复文本格式化器，专门处理常见 Markdown 标记，如标题、加粗、列表和多余空行。`ShieldPipelineService` 在拿到模型原始回复并经过输出防护后，对展示用文本做统一清洗，前端模板不引入 Markdown 渲染库。

**Tech Stack:** Java 21、Spring Boot 3、JUnit 5

---

### Task 1: 为回复文本格式化写失败测试

**Files:**
- Create: `src/test/java/com/shieldlm/output/ReplyTextFormatterTests.java`
- Modify: `src/test/java/com/shieldlm/core/ShieldPipelineServiceTests.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
void removesCommonMarkdownMarkers() {
    String markdown = "**标题**\n\n1. **步骤一**：说明";
    assertThat(formatter.format(markdown)).isEqualTo("标题\n\n1. 步骤一：说明");
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ReplyTextFormatterTests,ShieldPipelineServiceTests test`
Expected: FAIL，提示格式化器不存在或当前输出仍保留 Markdown 标记。

- [ ] **Step 3: Write minimal implementation**

```java
public class ReplyTextFormatter {
    public String format(String text) {
        // 去掉常见 Markdown 标记并收口空白
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ReplyTextFormatterTests,ShieldPipelineServiceTests test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/shieldlm/output src/test/java/com/shieldlm/output src/test/java/com/shieldlm/core/ShieldPipelineServiceTests.java
git commit -m "feat: normalize markdown style replies"
```
