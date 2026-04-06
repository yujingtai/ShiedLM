package com.shieldlm.config;

import com.shieldlm.core.RiskScoringService;
import com.shieldlm.core.ShieldPipelineService;
import com.shieldlm.core.StrategyService;
import com.shieldlm.core.model.AttackType;
import com.shieldlm.detection.InputDetectionService;
import com.shieldlm.detection.PromptRule;
import com.shieldlm.model.ApiModelClient;
import com.shieldlm.model.MockModelClient;
import com.shieldlm.model.ModelClient;
import com.shieldlm.output.OutputGuardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ShieldLmConfiguration {

    /**
     * MVP 阶段先把规则写成固定 Bean，优先保证演示链路稳定可跑。
     * 等后面做规则页时，再把它迁移到外部配置文件。
     */
    @Bean
    public List<PromptRule> promptRules() {
        return List.of(
                new PromptRule(
                        "IGNORE_PREVIOUS_RULES",
                        AttackType.PRIVILEGE_OVERRIDE,
                        30,
                        List.of("忽略之前", "ignore previous", "bypass policy")
                ),
                new PromptRule(
                        "SYSTEM_PROMPT_LEAK",
                        AttackType.PROMPT_EXTRACTION,
                        40,
                        List.of("系统提示词", "system prompt", "hidden instructions")
                ),
                new PromptRule(
                        "INTERNAL_CONFIG",
                        AttackType.SENSITIVE_DATA_INDUCTION,
                        30,
                        List.of("内部配置", "internal config", "api key")
                )
        );
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
            RiskScoringService riskScoringService,
            StrategyService strategyService
    ) {
        return new InputDetectionService(promptRules, riskScoringService, strategyService);
    }

    @Bean
    public OutputGuardService outputGuardService() {
        return new OutputGuardService();
    }

    @Bean
    public ModelClient modelClient(@Value("${shieldlm.model.mode:mock}") String mode) {
        if ("api".equalsIgnoreCase(mode)) {
            return new ApiModelClient();
        }
        return new MockModelClient();
    }

    @Bean
    public ShieldPipelineService shieldPipelineService(
            InputDetectionService inputDetectionService,
            ModelClient modelClient,
            OutputGuardService outputGuardService
    ) {
        return new ShieldPipelineService(inputDetectionService, modelClient, outputGuardService);
    }
}
