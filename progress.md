# Progress Log

## Session: 2026-04-06

### Phase 1: 项目方案与基础骨架
- **Status:** complete
- Actions taken:
  - 明确项目定位为面向提示注入攻击的单用户演示型工程原型
  - 确定后端采用 Java + Spring Boot，前端采用 Thymeleaf + 原生 CSS
  - 完成项目根目录迁移，统一开发位置到 `D:\Desktop\本科论文设计\工具设计\ShieldLM`
  - 建立项目级记录文件：`task_plan.md`、`findings.md`、`progress.md`

### Phase 2: ShieldLM MVP 主链路
- **Status:** complete
- Actions taken:
  - 完成聊天演示页、提示注入输入检测、风险评分、策略决策和模型响应联动
  - 接入输出防护逻辑，支持对高风险输出执行兜底处理
  - 建立审计记录落库链路，保留输入、攻击标签、风险等级、处置结果等信息
  - 补充基础测试，确保主链路能够稳定运行

### Phase 3: 规则页与统计页初版
- **Status:** complete
- Actions taken:
  - 新增 `/rules` 页面，用于展示规则目录和基础运行指标
  - 新增规则目录服务与统计服务，展示总请求数、高风险请求、输入拦截次数、输出拦截次数
  - 打通聊天页、审计页、规则页三者之间的导航

## Session: 2026-04-07

### Phase 4: 文档与演示材料整理
- **Status:** complete
- Actions taken:
  - 重写 `README.md`，补充项目目标、启动方式、页面入口与能力边界
  - 新增 `docs/demo-scenarios.md`，整理稳定可复现的演示样例
  - 新增 `docs/demo-script.md`，整理答辩演示顺序和讲解重点
  - 新增 `docs/commit-playbook.md`，统一后续提交粒度和提交信息风格
- Verification:
  - `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository test` -> Tests run: 11, Failures: 0, Errors: 0, Skipped: 0

### Phase 5: 审计日志搜索与分页
- **Status:** complete
- Actions taken:
  - 完成审计页“数据库查询分页 + 关键词搜索”增强方案设计
  - 更新仓储层、服务层、控制器与页面模板，支持关键词、风险等级、处置动作联合筛选
  - 补充分页导航与结果摘要，提升审计页可用性
- Files created/modified:
  - `docs/specs/2026-04-07-audit-search-pagination-design.md` (created)
  - `docs/plans/2026-04-07-audit-search-pagination-implementation.md` (created)
  - `src/main/java/com/shieldlm/audit/AuditController.java` (updated)
  - `src/main/java/com/shieldlm/audit/AuditLogService.java` (updated)
  - `src/main/java/com/shieldlm/audit/AuditRecordRepository.java` (updated)
  - `src/main/resources/templates/audit.html` (updated)
  - `src/main/resources/static/css/app.css` (updated)
  - `src/test/java/com/shieldlm/audit/AuditControllerTests.java` (updated)
- Verification:
  - `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=AuditControllerTests test` -> Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
  - `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository test` -> Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

### Phase 6: 规则页统计增强
- **Status:** complete
- Actions taken:
  - 扩展 `StatisticsSummary`，补充攻击类型分布和规则命中排行两类统计结果
  - 新增 `AttackTypeStatView` 与 `RuleHitStatView`，统一规则页统计展示模型
  - 调整 `StatisticsService`，基于审计记录聚合攻击类型次数，并映射到规则命中排行
  - 先修改测试断言，再补齐规则页模板和样式，保证页面行为与测试保持一致
  - 规则页新增“攻击类型分布”和“规则命中排行”两个板块，同时保留原有规则表
- Files created/modified:
  - `docs/specs/2026-04-07-rules-analytics-design.md` (created)
  - `docs/plans/2026-04-07-rules-analytics-implementation.md` (created)
  - `src/main/java/com/shieldlm/rules/AttackTypeStatView.java` (created)
  - `src/main/java/com/shieldlm/rules/RuleHitStatView.java` (created)
  - `src/main/java/com/shieldlm/rules/StatisticsSummary.java` (updated)
  - `src/main/java/com/shieldlm/rules/StatisticsService.java` (updated)
  - `src/main/resources/templates/rules.html` (updated)
  - `src/main/resources/static/css/app.css` (updated)
  - `src/test/java/com/shieldlm/rules/StatisticsServiceTests.java` (updated)
  - `src/test/java/com/shieldlm/rules/RulesControllerTests.java` (updated)
- Verification:
  - `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=RulesControllerTests test` -> 初次运行失败，确认模板尚未接入新统计板块
  - `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=RulesControllerTests,StatisticsServiceTests test` -> Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
  - `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository test` -> Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

## Current Status
- 聊天演示页、审计日志页、规则与统计页三块核心页面均已跑通
- 审计页已支持搜索与分页
- 规则页已支持基础指标、攻击类型分布和规则命中排行展示
- 当前仍未提交的这批内容主要是“规则页统计增强”相关代码和文档
