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
        // 审计页直接读取最近记录，用于展示系统留痕结果。
        model.addAttribute("records", auditLogService.findRecent());
        return "audit";
    }
}
