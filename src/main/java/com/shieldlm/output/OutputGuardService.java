package com.shieldlm.output;

import java.util.Locale;

public class OutputGuardService {

    public GuardedOutput guard(String rawReply) {
        String text = rawReply == null ? "" : rawReply;
        String normalizedText = text.toLowerCase(Locale.ROOT);

        if (containsAny(normalizedText, "sk-", "api key", "access token", "authorization: bearer", "bearer ")
                || containsAny(text, "密钥", "访问令牌", "授权头")) {
            return new GuardedOutput(
                    text,
                    "检测到疑似敏感信息泄露，系统已拦截本轮输出。",
                    true,
                    OutputRiskType.SECRET_LEAK
            );
        }
        if (containsAny(normalizedText, "system prompt", "developer message", "hidden instruction")
                || containsAny(text, "系统提示词", "隐藏指令", "开发者消息", "前置规则", "底层指令", "内部提示")) {
            return new GuardedOutput(
                    text,
                    "检测到疑似内部提示信息泄露，系统已拦截本轮输出。",
                    true,
                    OutputRiskType.PROMPT_LEAK
            );
        }
        if (containsAny(text, "色情网站", "违法链接", "成人资源", "成人资源站", "下载地址", "资源链接")) {
            return new GuardedOutput(
                    text,
                    "检测到不安全内容输出，系统已拦截本轮输出。",
                    true,
                    OutputRiskType.UNSAFE_CONTENT
            );
        }
        return new GuardedOutput(text, text, false, OutputRiskType.NONE);
    }

    private boolean containsAny(String text, String... fragments) {
        for (String fragment : fragments) {
            if (text.contains(fragment)) {
                return true;
            }
        }
        return false;
    }
}
