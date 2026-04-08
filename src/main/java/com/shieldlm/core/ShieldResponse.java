package com.shieldlm.core;

import com.shieldlm.core.model.ShieldDecision;
import com.shieldlm.output.OutputRiskType;

public record ShieldResponse(
        String userPrompt,
        ShieldDecision decision,
        String rawReply,
        String finalReply,
        boolean outputBlocked,
        OutputRiskType outputRiskType
) {

    public ShieldResponse(
            String userPrompt,
            ShieldDecision decision,
            String rawReply,
            String finalReply,
            boolean outputBlocked
    ) {
        this(
                userPrompt,
                decision,
                rawReply,
                finalReply,
                outputBlocked,
                outputBlocked ? OutputRiskType.PROMPT_LEAK : OutputRiskType.NONE
        );
    }
}
