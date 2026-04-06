package com.shieldlm.web;

import com.shieldlm.web.dto.ChatForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/")
    public String chat(Model model) {
        // chatForm 是页面表单绑定对象，后续用户输入会通过它提交到后端。
        model.addAttribute("chatForm", new ChatForm());
        return "chat";
    }
}
