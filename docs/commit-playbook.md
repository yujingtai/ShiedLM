# ShieldLM 提交说明

本文档用于约定项目开发过程中的提交粒度和提交方式，保证开发记录清晰可回溯。

## 提交原则

- 每完成一个小而完整的功能批次就提交一次
- 每次提交尽量只解决一类问题，避免把多个主题混在一起
- 提交前先做最基本的校验，确保当前版本处于可用状态

## 推荐的提交类型

- `feat`：新增功能
- `fix`：修复问题
- `docs`：补充文档
- `chore`：非业务功能调整，例如文案整理、配置微调
- `test`：补充或调整测试

## 建议的提交粒度

- 完成输入检测能力后提交
- 完成风险评分或策略执行后提交
- 完成某一个页面功能后提交
- 完成一批文档补充后提交

## 推荐流程

1. 完成功能或文档改动
2. 运行对应校验命令
3. 执行 `git status` 检查改动范围
4. 使用清晰的提交信息提交
5. 推送到远程仓库保留开发记录

## 提交信息示例

```text
feat: add audit filters and summaries
feat: enrich chat demo scenarios
docs: add project runbook and demo materials
chore: refine project wording and isolate test db
```

## 当前约定

本项目继续沿用“小批次开发、小批次提交、小批次推送”的节奏推进，优先保留连续的开发痕迹，而不是等所有内容做完再一次性提交。
