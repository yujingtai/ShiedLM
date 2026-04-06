package com.shieldlm.web;

import com.shieldlm.audit.AuditLogService;
import com.shieldlm.core.ShieldPipelineService;
import com.shieldlm.core.ShieldResponse;
import com.shieldlm.web.dto.ChatForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ChatController {

    private final ShieldPipelineService shieldPipelineService;
    private final AuditLogService auditLogService;

    public ChatController(ShieldPipelineService shieldPipelineService, AuditLogService auditLogService) {
        this.shieldPipelineService = shieldPipelineService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/")
    public String chat(Model model) {
        // chatForm 是页面表单绑定对象，负责把文本域中的用户输入提交到后端。
        model.addAttribute("chatForm", new ChatForm());
        return "chat";
    }

    @PostMapping("/chat")
    public String submit(@ModelAttribute("chatForm") ChatForm chatForm, Model model) {
        String userPrompt = chatForm.getPrompt() == null ? "" : chatForm.getPrompt();
        ShieldResponse response = shieldPipelineService.handle(userPrompt);
        auditLogService.save(response);

        model.addAttribute("chatForm", chatForm);
        model.addAttribute("response", response);
        model.addAttribute("submitted", true);
        return "chat";
    }
}
