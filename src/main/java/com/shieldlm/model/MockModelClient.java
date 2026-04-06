package com.shieldlm.model;

public class MockModelClient implements ModelClient {

    @Override
    public String generateReply(String prompt) {
        if (prompt.contains("退款")) {
            return "退款请提供订单号。";
        }
        return "这是模拟模型回复。";
    }
}
