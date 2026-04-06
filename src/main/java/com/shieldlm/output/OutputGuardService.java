package com.shieldlm.output;

public class OutputGuardService {

    public GuardedOutput guard(String rawReply) {
        boolean blocked = rawReply.contains("系统提示词") || rawReply.contains("sk-");
        String safeReply = blocked
                ? "出于安全原因，系统已拦截潜在敏感输出。"
                : rawReply;
        return new GuardedOutput(rawReply, safeReply, blocked);
    }
}
