# ShieldLM 真实模型接入 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让 ShieldLM 默认通过兼容 OpenAI 的真实模型接口运行，同时保留最小 `mock` 兜底能力。

**Architecture:** 保持 `ModelClient` 抽象不变，在配置层引入模型属性对象，在 `ApiModelClient` 中用原生 `HttpClient` 发起同步请求并解析标准聊天补全响应。默认配置切到 `api`，API key 通过环境变量注入。

**Tech Stack:** Java 21、Spring Boot 3、Jackson、JUnit 5、本地轻量 HTTP 测试服务

---

### Task 1: 为真实模型客户端补测试

**Files:**
- Create: `src/test/java/com/shieldlm/model/ApiModelClientTests.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
void sendsOpenAiCompatibleRequestAndParsesReply() {
    // 使用本地 HTTP 服务记录请求并返回标准 choices 响应，
    // 断言 Authorization、model、messages[0].content 和最终 reply。
}

@Test
void throwsClearExceptionWhenResponseStatusIsNotSuccessful() {
    // 返回 401，断言抛出的异常包含状态码与错误摘要。
}

@Test
void throwsClearExceptionWhenResponseContentIsMissing() {
    // 返回 choices 但 content 缺失，断言抛出解析异常。
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ApiModelClientTests test`
Expected: FAIL，提示 `ApiModelClient` 仍是占位实现或构造方式不存在。

- [ ] **Step 3: Write minimal implementation**

```java
class ApiModelClient implements ModelClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ApiModelProperties properties;

    @Override
    public String generateReply(String prompt) {
        // 构造 POST /v1/chat/completions 请求并解析 choices[0].message.content
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ApiModelClientTests test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/test/java/com/shieldlm/model/ApiModelClientTests.java src/main/java/com/shieldlm/model/ApiModelClient.java
git commit -m "feat: add api model client"
```

### Task 2: 补模型配置对象与装配逻辑

**Files:**
- Create: `src/main/java/com/shieldlm/config/ApiModelProperties.java`
- Modify: `src/main/java/com/shieldlm/config/ShieldLmConfiguration.java`
- Test: `src/test/java/com/shieldlm/config/ShieldLmConfigurationTests.java`

- [ ] **Step 1: Write the failing test**

```java
@Test
void createsApiModelClientWhenModeIsApi() {
    // 传入 api 配置后断言返回 ApiModelClient。
}

@Test
void createsMockModelClientWhenModeIsMock() {
    // 传入 mock 配置后断言返回 MockModelClient。
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ShieldLmConfigurationTests test`
Expected: FAIL，提示缺少配置对象或 Bean 装配不匹配。

- [ ] **Step 3: Write minimal implementation**

```java
@ConfigurationProperties(prefix = "shieldlm.model")
public class ApiModelProperties {
    private String mode = "api";
    private String baseUrl = "https://ai.td.ee";
    private String modelName = "gpt5.4";
    private String apiKey;
    private String chatPath = "/v1/chat/completions";
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ShieldLmConfigurationTests test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/shieldlm/config/ApiModelProperties.java src/main/java/com/shieldlm/config/ShieldLmConfiguration.java src/test/java/com/shieldlm/config/ShieldLmConfigurationTests.java
git commit -m "feat: add api model configuration"
```

### Task 3: 切换默认配置并补运行说明

**Files:**
- Modify: `src/main/resources/application.yml`
- Modify: `README.md`

- [ ] **Step 1: Write the failing test**

```text
本任务以运行配置校验为主，不单独新增自动化测试，
通过已有配置测试与最终集成测试覆盖默认模式切换行为。
```

- [ ] **Step 2: Run test to verify current gap**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ShieldLmConfigurationTests,ShieldLmApplicationTests test`
Expected: FAIL 或仍显示默认模式为 mock，需要调整配置说明。

- [ ] **Step 3: Write minimal implementation**

```yml
shieldlm:
  model:
    mode: api
    base-url: https://ai.td.ee
    model-name: gpt5.4
    api-key: ${SHIELDLM_API_KEY:}
    chat-path: /v1/chat/completions
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ShieldLmConfigurationTests,ShieldLmApplicationTests test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/application.yml README.md
git commit -m "docs: document real model startup"
```

### Task 4: 做最终回归验证

**Files:**
- Modify if needed: `src/test/java/com/shieldlm/ShieldLmApplicationTests.java`

- [ ] **Step 1: Run focused regression tests**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository -Dtest=ApiModelClientTests,ShieldLmConfigurationTests,ShieldLmApplicationTests,ChatControllerTests test`
Expected: PASS

- [ ] **Step 2: Run full test suite**

Run: `mvn -Dmaven.repo.local=C:\Users\wjh\.m2\repository test`
Expected: PASS

- [ ] **Step 3: Commit final integration**

```bash
git add src/main/java/com/shieldlm/config src/main/java/com/shieldlm/model src/main/resources/application.yml README.md src/test/java/com/shieldlm
git commit -m "feat: connect shieldlm to real model api"
```
