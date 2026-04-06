package com.shieldlm.core;

import com.shieldlm.core.model.ShieldDecision;

public record ShieldResponse(
        String userPrompt,
        ShieldDecision decision,
        String rawReply,
        String finalReply,
        boolean outputBlocked
) {
}
