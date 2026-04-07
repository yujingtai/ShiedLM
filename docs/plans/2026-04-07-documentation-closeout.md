# ShieldLM Documentation Closeout Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补齐项目运行说明、演示脚本与提交说明，让工程原型具备更完整的交付材料。

**Architecture:** 本批次不改动核心业务代码，只补充仓库级文档。文档围绕三个目标展开：说明项目是什么、如何启动与演示、如何理解当前开发记录。

**Tech Stack:** Markdown、Git、Maven

---

### Task 1: 重写 README

**Files:**
- Modify: `README.md`

- [ ] **Step 1: 整理 README 必须包含的章节**

```text
项目简介
项目目标
当前能力
技术栈
项目结构
快速启动
演示入口
运行模式
已实现与未实现边界
开发记录
```

- [ ] **Step 2: 写入 README 内容**

```md
在 README 中补齐启动命令、页面入口、当前能力边界和文档索引。
```

- [ ] **Step 3: 校验 README 关键章节存在**

Run: `rg -n "## 项目目标|## 快速启动|## 演示入口|## 已实现与未实现边界" README.md`
Expected: PASS，输出对应章节标题

### Task 2: 新增演示材料

**Files:**
- Create: `docs/demo-scenarios.md`
- Create: `docs/demo-script.md`

- [ ] **Step 1: 整理可稳定演示的样例**

```text
正常业务咨询
规则绕过尝试
混合型提示注入攻击
输出侧兜底
```

- [ ] **Step 2: 写入样例文档与演示脚本**

```md
为每个样例补输入、预期结果和展示重点。
为演示流程补“先讲什么、再演什么、最后怎么看审计页和规则页”的顺序。
```

- [ ] **Step 3: 校验新文档存在**

Run: `rg -n "样例 1|样例 2|演示主线|演示前检查" docs/demo-scenarios.md docs/demo-script.md`
Expected: PASS，输出命中行

### Task 3: 新增提交说明并同步项目进度

**Files:**
- Create: `docs/commit-playbook.md`
- Modify: `task_plan.md`
- Modify: `progress.md`
- Modify: `findings.md`

- [ ] **Step 1: 写入提交说明**

```md
说明提交粒度、提交流程和示例提交信息。
```

- [ ] **Step 2: 同步规划文件**

```text
在 task_plan.md 中记录文档与演示闭环这一批次。
在 progress.md 中追加本次改动和校验记录。
在 findings.md 中补充当前文档交付结论。
```

- [ ] **Step 3: 执行校验**

Run: `rg -n "提交原则|推荐流程" docs/commit-playbook.md`
Expected: PASS，输出章节标题
