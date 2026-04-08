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

import java.util.List;

@Controller
public class ChatController {

    private final ShieldPipelineService shieldPipelineService;
    private final AuditLogService auditLogService;
    private final DemoScenarioService demoScenarioService;

    public ChatController(
            ShieldPipelineService shieldPipelineService,
            AuditLogService auditLogService,
            DemoScenarioService demoScenarioService
    ) {
        this.shieldPipelineService = shieldPipelineService;
        this.auditLogService = auditLogService;
        this.demoScenarioService = demoScenarioService;
    }

    @ModelAttribute("demoScenarios")
    public List<DemoScenario> demoScenarios() {
        return demoScenarioService.listScenarios();
    }

    @GetMapping("/")
    public String chat(Model model) {
        model.addAttribute("chatForm", new ChatForm());
        return "chat";
    }

    @PostMapping("/chat")
    public String submit(@ModelAttribute("chatForm") ChatForm chatForm, Model model) {
        String userPrompt = chatForm.getPrompt() == null ? "" : chatForm.getPrompt();
        model.addAttribute("chatForm", chatForm);
        try {
            ShieldResponse response = shieldPipelineService.handle(userPrompt);
            auditLogService.save(response);

            model.addAttribute("response", response);
            model.addAttribute("submitted", true);
        } catch (IllegalStateException exception) {
            model.addAttribute("submitted", false);
            model.addAttribute("errorMessage", exception.getMessage());
        } catch (RuntimeException exception) {
            model.addAttribute("submitted", false);
            model.addAttribute("errorMessage", exception.getMessage());
        }
        return "chat";
    }
}
