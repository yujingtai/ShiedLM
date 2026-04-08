package com.shieldlm.output;

public record GuardedOutput(
        String rawReply,
        String safeReply,
        boolean blocked,
        OutputRiskType riskType
) {

    public GuardedOutput(String rawReply, String safeReply, boolean blocked) {
        this(rawReply, safeReply, blocked, blocked ? OutputRiskType.PROMPT_LEAK : OutputRiskType.NONE);
    }
}
