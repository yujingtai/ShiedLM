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
        ShieldDecision decision = inputDetectionService.analyze(userPrompt);
        if (decision.action() == DefenseAction.BLOCK) {
            return new ShieldResponse(userPrompt, decision, "", "请求存在高风险，已被系统拦截。", false);
        }

        String rawReply = modelClient.generateReply(decision.processedPrompt());
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
