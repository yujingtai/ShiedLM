package com.shieldlm.web.dto;

public class ChatForm {

    /**
     * 用户在聊天框中输入的原始问题。
     * 后续接入完整防护链路时，会先拿这个字段做输入检测。
     */
    private String prompt = "";

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
