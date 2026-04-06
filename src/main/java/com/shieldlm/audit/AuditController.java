package com.shieldlm.audit;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditController {

    private final AuditLogService auditLogService;

    public AuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/audit")
    public String audit(Model model) {
        // 审计页直接读取最近记录，便于答辩时展示系统已经完成留痕。
        model.addAttribute("records", auditLogService.findRecent());
        return "audit";
    }
}
