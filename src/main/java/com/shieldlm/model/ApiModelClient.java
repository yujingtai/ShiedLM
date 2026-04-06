package com.shieldlm.model;

public class ApiModelClient implements ModelClient {

    @Override
    public String generateReply(String prompt) {
        throw new UnsupportedOperationException("真实 API 模式将在后续接入。");
    }
}
