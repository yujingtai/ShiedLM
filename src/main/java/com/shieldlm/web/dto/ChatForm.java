package com.shieldlm.web.dto;

public class ChatForm {

    /**
     * 用户在聊天框中输入的原始提示词。
     * 后续主链路会先检测这个字段，再决定是否改写、拦截或放行。
     */
    private String prompt = "";

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
