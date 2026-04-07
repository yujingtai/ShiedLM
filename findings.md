# Findings & Decisions

## Requirements
- 项目必须围绕“面向大模型的提示注入攻击”这一论文主题展开，不能过度收缩成单一业务系统。
- 需要有一个可实际演示的工程原型，便于开题与答辩时向老师展示。
- 采用单用户演示模式即可，不需要登录、权限、多用户协作。
- 后端使用 Java，整体技术方案由我主导。
- 开发过程要保留连续的 Git 记录，尽量一小功能一提交。
- 代码要有适量注释，重点解释变量含义、数据流和关键处理逻辑。
- 需要在某些节点主动向用户汇报当前开发进度和已完成范围。

## Research Findings
- 当前项目已形成较稳定的 MVP 结构：聊天演示页、输入检测、风险评分、策略决策、模型输出防护、审计落库。
- `planning-with-files` 适合当前这种多轮开发任务，可作为项目级“进度盘”和会话恢复辅助。
- 规则与统计页已经接入，现阶段前台已有三块核心页面，可支持答辩时从“攻击输入”一路讲到“审计留痕”和“整体效果统计”。
- 当前最值得继续推进的是“答辩演示路径”，包括攻击样例、正常样例和页面文案收口。

## Technical Decisions
| Decision | Rationale |
|----------|-----------|
| 使用 H2 + Spring Data JPA 做审计留痕 | 无需额外部署数据库，演示成本低 |
| 使用 MockModelClient 驱动演示 | 不依赖真实模型接口，更适合短周期毕设原型 |
| 统计能力基于审计表实时聚合 | 逻辑简单，能快速展示系统效果 |
| 在项目根目录新增 `task_plan.md` / `findings.md` / `progress.md` | 让用户和我都能快速恢复开发上下文 |
| 在规则页同时展示规则表和关键指标卡片 | 对老师更直观，方便讲“系统如何识别”和“系统效果如何” |

## Issues Encountered
| Issue | Resolution |
|-------|------------|
| 早期部分中文文案出现编码损坏 | 通过重写相关文件并统一中文文案修正 |
| 远程 push 不稳定 | 记录本地提交号，必要时由用户手动推送 |

## Resources
- 项目计划文档：`docs/plans/2026-04-06-shieldlm-mvp.md`
- 项目设计文档：`docs/specs/2026-04-06-shieldlm-design.md`
- 项目进度盘：`task_plan.md`、`findings.md`、`progress.md`
- 新安装 skill：`C:\Users\wjh\.codex\skills\planning-with-files\SKILL.md`

## Visual/Browser Findings
- `planning-with-files` 仓库支持 Codex，并强调用 `task_plan.md`、`findings.md`、`progress.md` 做持久化计划与恢复。
- 该 skill 更适合作为“辅助记忆层”，不取代项目中已有的正式设计文档和实现计划。
## 2026-04-07 Documentation Findings
- 当前代码主链路已经可运行，当前缺口主要集中在交付文档与演示材料。
- README 之前内容过少，不足以支撑独立启动和复现。
- 当前最适合固定的演示顺序是：正常请求 -> 中风险改写 -> 高风险拦截 -> 输出兜底。
- 本批次优先补文档，不改业务代码，可以较低风险提升项目完整度。
