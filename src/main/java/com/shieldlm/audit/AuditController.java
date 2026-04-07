package com.shieldlm.audit;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class AuditController {

    private final AuditLogService auditLogService;

    public AuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/audit")
    public String audit(
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) DefenseAction defenseAction,
            Model model
    ) {
        List<AuditRecord> records = riskLevel == null && defenseAction == null
                ? auditLogService.findRecent()
                : auditLogService.findRecent(riskLevel, defenseAction);

        model.addAttribute("records", records);
        model.addAttribute("riskLevels", Arrays.asList(RiskLevel.values()));
        model.addAttribute("defenseActions", Arrays.asList(DefenseAction.values()));
        model.addAttribute("selectedRiskLevel", riskLevel);
        model.addAttribute("selectedDefenseAction", defenseAction);
        model.addAttribute("displayedCount", (long) records.size());
        model.addAttribute("blockedCount", records.stream()
                .filter(record -> record.getDefenseAction() == DefenseAction.BLOCK)
                .count());
        model.addAttribute("outputBlockedCount", records.stream()
                .filter(AuditRecord::isOutputBlocked)
                .count());
        return "audit";
    }
}
