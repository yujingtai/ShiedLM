# ShieldLM

ShieldLM 是一个围绕提示注入攻击检测与防护构建的本科毕业设计工程原型。项目采用 Spring Boot 单体架构，围绕“输入检测、风险评分、分级防御、输出审查、审计留痕”这条主链路，提供可运行、可演示、可扩展的安全防护示例。

## 项目目标

- 面向大模型提示注入攻击构建一套可解释的防护流程
- 用单用户演示原型验证混合型攻击场景下的识别与处置能力
- 为论文撰写、开题答辩和最终展示提供可复现的工程材料

## 当前能力

- 输入侧提示注入检测
- 风险评分与风险等级划分
- 分级防御动作：放行、改写、拦截
- 模型输出二次审查与兜底回复
- 审计日志持久化与历史查询
- 规则页与基础统计页展示
- 内置演示样例，便于快速复现典型场景

## 技术栈

- Java 21
- Spring Boot 3
- Thymeleaf
- Spring Data JPA
- H2 Database
- JUnit 5 / MockMvc
- 原生 JavaScript + 自定义 CSS

## 项目结构

```text
src/main/java/com/shieldlm
├─ audit       审计日志、查询与页面控制器
├─ config      Bean 装配与运行配置
├─ core        防护主链路、评分与策略执行
├─ detection   输入规则检测
├─ model       模型适配层
├─ output      输出审查
├─ rules       规则目录与统计展示
└─ web         聊天演示页与演示样例

src/main/resources
├─ rules       提示注入规则配置
├─ static      CSS / JS 资源
└─ templates   聊天页、审计页、规则页模板
```

## 快速启动

### 环境要求

- JDK 21
- Maven 3.6+

### 启动命令

```bash
mvn spring-boot:run
```

默认端口为 `8080`，启动后可访问以下页面：

- `http://localhost:8080/`：聊天演示页
- `http://localhost:8080/audit`：审计日志页
- `http://localhost:8080/rules`：规则与统计页
- `http://localhost:8080/h2-console`：H2 控制台

### 测试命令

```bash
mvn test
```

## 演示入口

推荐先从聊天页开始，依次展示正常请求、中风险绕过、高风险混合注入和输出兜底四类场景。完整材料见：

- `docs/demo-scenarios.md`
- `docs/demo-script.md`

## 当前运行模式

项目默认使用兼容 OpenAI 协议的真实模型接口，配置位于 `src/main/resources/application.yml`：

```yml
shieldlm:
  model:
    mode: ${SHIELDLM_MODEL_MODE:api}
    base-url: ${SHIELDLM_BASE_URL:https://api.deepseek.com}
    model-name: ${SHIELDLM_MODEL_NAME:deepseek-chat}
    api-key: ${SHIELDLM_API_KEY:}
    chat-path: ${SHIELDLM_CHAT_PATH:/chat/completions}
```

项目启动前至少需要设置：

- `SHIELDLM_API_KEY`

如果你需要切回演示兜底模式，可以临时设置：

- `SHIELDLM_MODEL_MODE=mock`

PowerShell 启动示例：

```powershell
$env:SHIELDLM_API_KEY="你的私有 key"
mvn spring-boot:run
```

当前默认地址为 `https://api.deepseek.com`，默认模型为 `deepseek-chat`。

## 已实现与未实现边界

### 已实现

- 防护主链路可以完整跑通
- 已接入兼容 OpenAI 的真实模型调用
- 聊天、审计、规则三个页面均可访问
- 审计记录支持风险等级与防御动作筛选
- 页面已内置演示样例，可快速复现场景

### 暂未完成

- 流式输出与多轮上下文暂未接入
- 审计页尚未加入关键词搜索、分页、导出能力
- 统计页目前以基础指标为主，尚未补充趋势图与更细粒度分析

## 开发记录

项目按“小批次完成功能再提交”的方式推进，便于保留连续开发记录。提交说明见：

- `docs/commit-playbook.md`
