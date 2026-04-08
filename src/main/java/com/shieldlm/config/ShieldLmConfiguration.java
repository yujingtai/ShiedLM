package com.shieldlm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shieldlm.core.RiskScoringService;
import com.shieldlm.core.ShieldPipelineService;
import com.shieldlm.core.StrategyService;
import com.shieldlm.core.model.AttackType;
import com.shieldlm.detection.InputDetectionService;
import com.shieldlm.detection.ModelSemanticSafetyJudge;
import com.shieldlm.detection.PromptNormalizer;
import com.shieldlm.detection.PromptRule;
import com.shieldlm.detection.RiskSignal;
import com.shieldlm.detection.RiskSignalExtractor;
import com.shieldlm.detection.SemanticSafetyJudge;
import com.shieldlm.model.ApiModelClient;
import com.shieldlm.model.MockModelClient;
import com.shieldlm.model.ModelClient;
import com.shieldlm.output.OutputGuardService;
import com.shieldlm.output.ReplyTextFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.List;

@Configuration
public class ShieldLmConfiguration {

    @Bean
    public List<PromptRule> promptRules() {
        return List.of(
                new PromptRule(
                        "IGNORE_PREVIOUS_RULES",
                        AttackType.PRIVILEGE_OVERRIDE,
                        30,
                        List.of(RiskSignal.OVERRIDE_INTENT),
                        List.of("忽略之前", "ignore previous", "不要遵守前面的限制", "绕过限制", "无视规则")
                ),
                new PromptRule(
                        "ROLE_OVERRIDE",
                        AttackType.PRIVILEGE_OVERRIDE,
                        25,
                        List.of(RiskSignal.OVERRIDE_INTENT, RiskSignal.ROLE_HIJACK_INTENT),
                        List.of("现在你是", "扮演", "切换身份", "充当")
                ),
                new PromptRule(
                        "PROMPT_THEFT",
                        AttackType.PROMPT_EXTRACTION,
                        35,
                        List.of(RiskSignal.PROMPT_LEAK_INTENT),
                        List.of("系统提示词", "system prompt", "隐藏指令", "开发者消息", "developer message")
                ),
                new PromptRule(
                        "SECRET_EXTRACTION",
                        AttackType.SENSITIVE_DATA_INDUCTION,
                        30,
                        List.of(RiskSignal.SECRET_EXTRACTION_INTENT),
                        List.of("内部配置", "internal config", "api key", "密钥", "token", "access key")
                ),
                new PromptRule(
                        "SEXUAL_ILLEGAL_CONTENT",
                        AttackType.ILLEGAL_HARMFUL_CONTENT,
                        45,
                        List.of(RiskSignal.UNSAFE_CONTENT_INTENT),
                        List.of("色情", "成人网站", "黄网站", "18+网站", "成人影视", "av网站", "儿童色情")
                ),
                new PromptRule(
                        "VIOLENT_CRIME_GUIDANCE",
                        AttackType.ILLEGAL_HARMFUL_CONTENT,
                        45,
                        List.of(RiskSignal.VIOLENT_CRIME_INTENT),
                        List.of("杀人", "埋尸", "处理尸体", "毁尸灭迹", "碎尸", "下毒")
                ),
                new PromptRule(
                        "DRUG_CRIME_GUIDANCE",
                        AttackType.ILLEGAL_HARMFUL_CONTENT,
                        45,
                        List.of(RiskSignal.DRUG_CRIME_INTENT),
                        List.of("冰毒", "海洛因", "制毒", "炼毒", "毒品配方", "贩毒")
                ),
                new PromptRule(
                        "WEAPON_EXPLOSIVE_GUIDANCE",
                        AttackType.ILLEGAL_HARMFUL_CONTENT,
                        45,
                        List.of(RiskSignal.WEAPON_EXPLOSIVE_INTENT),
                        List.of("炸弹", "爆炸物", "土炸药", "雷管", "改枪", "枪支")
                ),
                new PromptRule(
                        "CYBER_CRIME_GUIDANCE",
                        AttackType.ILLEGAL_HARMFUL_CONTENT,
                        45,
                        List.of(RiskSignal.CYBER_CRIME_INTENT),
                        List.of("入侵", "破解密码", "盗取密码", "木马", "钓鱼网站", "勒索病毒")
                ),
                new PromptRule(
                        "FRAUD_THEFT_GUIDANCE",
                        AttackType.ILLEGAL_HARMFUL_CONTENT,
                        45,
                        List.of(RiskSignal.FRAUD_THEFT_INTENT),
                        List.of("诈骗", "电信诈骗", "诈骗话术", "盗窃", "盗刷", "洗钱", "伪造证件")
                )
        );
    }

    @Bean
    public PromptNormalizer promptNormalizer() {
        return new PromptNormalizer();
    }

    @Bean
    public RiskSignalExtractor riskSignalExtractor() {
        return new RiskSignalExtractor();
    }

    @Bean
    public RiskScoringService riskScoringService() {
        return new RiskScoringService();
    }

    @Bean
    public StrategyService strategyService() {
        return new StrategyService();
    }

    @Bean
    public InputDetectionService inputDetectionService(
            List<PromptRule> promptRules,
            PromptNormalizer promptNormalizer,
            RiskSignalExtractor riskSignalExtractor,
            RiskScoringService riskScoringService,
            StrategyService strategyService,
            SemanticSafetyJudge semanticSafetyJudge
    ) {
        return new InputDetectionService(
                promptRules,
                promptNormalizer,
                riskSignalExtractor,
                riskScoringService,
                strategyService,
                semanticSafetyJudge
        );
    }

    @Bean
    public SemanticSafetyJudge semanticSafetyJudge(ModelClient modelClient, ObjectMapper objectMapper) {
        return new ModelSemanticSafetyJudge(modelClient, objectMapper);
    }

    @Bean
    public OutputGuardService outputGuardService() {
        return new OutputGuardService();
    }

    @Bean
    public ReplyTextFormatter replyTextFormatter() {
        return new ReplyTextFormatter();
    }

    @Bean
    public ApiModelProperties apiModelProperties(
            @Value("${shieldlm.model.mode:api}") String mode,
            @Value("${shieldlm.model.base-url:https://api.deepseek.com}") String baseUrl,
            @Value("${shieldlm.model.model-name:deepseek-chat}") String modelName,
            @Value("${shieldlm.model.api-key:}") String apiKey,
            @Value("${shieldlm.model.chat-path:/chat/completions}") String chatPath
    ) {
        ApiModelProperties properties = new ApiModelProperties();
        properties.setMode(mode);
        properties.setBaseUrl(baseUrl);
        properties.setModelName(modelName);
        properties.setApiKey(apiKey);
        properties.setChatPath(chatPath);
        return properties;
    }

    @Bean
    public ModelClient modelClient(ApiModelProperties properties, ObjectMapper objectMapper) {
        if ("api".equalsIgnoreCase(properties.getMode())) {
            if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
                throw new IllegalStateException("缺少真实模型 API key 配置，请设置 SHIELDLM_API_KEY。");
            }
            return new ApiModelClient(HttpClient.newHttpClient(), objectMapper, properties);
        }
        return new MockModelClient();
    }

    @Bean
    public ShieldPipelineService shieldPipelineService(
            InputDetectionService inputDetectionService,
            ModelClient modelClient,
            OutputGuardService outputGuardService,
            ReplyTextFormatter replyTextFormatter
    ) {
        return new ShieldPipelineService(inputDetectionService, modelClient, outputGuardService, replyTextFormatter);
    }
}
