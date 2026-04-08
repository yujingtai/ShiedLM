package com.shieldlm.core;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.ShieldDecision;
import com.shieldlm.detection.InputDetectionService;
import com.shieldlm.model.ModelClient;
import com.shieldlm.output.GuardedOutput;
import com.shieldlm.output.OutputGuardService;
import com.shieldlm.output.ReplyTextFormatter;

public class ShieldPipelineService {

    private final InputDetectionService inputDetectionService;
    private final ModelClient modelClient;
    private final OutputGuardService outputGuardService;
    private final ReplyTextFormatter replyTextFormatter;

    public ShieldPipelineService(
            InputDetectionService inputDetectionService,
            ModelClient modelClient,
            OutputGuardService outputGuardService,
            ReplyTextFormatter replyTextFormatter
    ) {
        this.inputDetectionService = inputDetectionService;
        this.modelClient = modelClient;
        this.outputGuardService = outputGuardService;
        this.replyTextFormatter = replyTextFormatter;
    }

    public ShieldResponse handle(String userPrompt) {
        ShieldDecision decision = inputDetectionService.analyze(userPrompt);
        if (decision.action() == DefenseAction.BLOCK) {
            return new ShieldResponse(userPrompt, decision, "", "请求存在高风险，已被系统拦截。", false);
        }

        String rawReply = modelClient.generateReply(decision.processedPrompt());
        GuardedOutput guardedOutput = outputGuardService.guard(rawReply);
        String displayRawReply = replyTextFormatter.format(rawReply);
        String displayFinalReply = replyTextFormatter.format(guardedOutput.safeReply());
        return new ShieldResponse(
                userPrompt,
                decision,
                displayRawReply,
                displayFinalReply,
                guardedOutput.blocked(),
                guardedOutput.riskType()
        );
    }
}
