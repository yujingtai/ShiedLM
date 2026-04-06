package com.shieldlm.model;

public class MockModelClient implements ModelClient {

    @Override
    public String generateReply(String prompt) {
        if (prompt.contains("退款")) {
            return "退款申请需要提供订单号、付款时间和退款原因，我们会在 1 到 3 个工作日内处理。";
        }
        if (prompt.contains("系统提示词") || prompt.contains("system prompt")) {
            return "系统提示词是 internal-only，密钥前缀是 sk-demo";
        }
        return "这是模拟模型回复，可用于展示 ShieldLM 的输入检测与输出防护流程。";
    }
}
