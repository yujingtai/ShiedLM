package com.shieldlm.core;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.ShieldDecision;
import com.shieldlm.detection.InputDetectionService;
import com.shieldlm.model.ModelClient;
import com.shieldlm.output.GuardedOutput;
import com.shieldlm.output.OutputGuardService;

public class ShieldPipelineService {

    private final InputDetectionService inputDetectionService;
    private final ModelClient modelClient;
    private final OutputGuardService outputGuardService;

    public ShieldPipelineService(
            InputDetectionService inputDetectionService,
            ModelClient modelClient,
            OutputGuardService outputGuardService
    ) {
        this.inputDetectionService = inputDetectionService;
        this.modelClient = modelClient;
        this.outputGuardService = outputGuardService;
    }

    public ShieldResponse handle(String userPrompt) {
        // 第一步先分析用户输入，判断是否存在提示注入风险。
        ShieldDecision decision = inputDetectionService.analyze(userPrompt);
        if (decision.action() == DefenseAction.BLOCK) {
            return new ShieldResponse(userPrompt, decision, "", "请求存在高风险，已被系统拦截。", false);
        }

        // 如果允许继续，就把策略处理后的提示词交给模型生成回复。
        String rawReply = modelClient.generateReply(decision.processedPrompt());

        // 模型输出也要做二次检查，防止敏感信息在回复阶段泄漏。
        GuardedOutput guardedOutput = outputGuardService.guard(rawReply);
        return new ShieldResponse(
                userPrompt,
                decision,
                rawReply,
                guardedOutput.safeReply(),
                guardedOutput.blocked()
        );
    }
}
