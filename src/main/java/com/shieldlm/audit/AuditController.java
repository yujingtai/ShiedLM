package com.shieldlm.audit;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.springframework.data.domain.Page;
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
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) DefenseAction defenseAction,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<AuditRecord> recordPage = auditLogService.findRecent(keyword, riskLevel, defenseAction, page);
        List<AuditRecord> records = recordPage.getContent();

        model.addAttribute("records", records);
        model.addAttribute("riskLevels", Arrays.asList(RiskLevel.values()));
        model.addAttribute("defenseActions", Arrays.asList(DefenseAction.values()));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("selectedRiskLevel", riskLevel);
        model.addAttribute("selectedDefenseAction", defenseAction);
        model.addAttribute("displayedCount", recordPage.getTotalElements());
        model.addAttribute("totalMatches", recordPage.getTotalElements());
        model.addAttribute("pageNumber", recordPage.getNumber());
        model.addAttribute("pageNumberDisplay", recordPage.getTotalPages() == 0 ? 0 : recordPage.getNumber() + 1);
        model.addAttribute("totalPages", recordPage.getTotalPages());
        model.addAttribute("hasPrevious", recordPage.hasPrevious());
        model.addAttribute("hasNext", recordPage.hasNext());
        model.addAttribute("blockedCount", records.stream()
                .filter(record -> record.getDefenseAction() == DefenseAction.BLOCK)
                .count());
        model.addAttribute("outputBlockedCount", records.stream()
                .filter(AuditRecord::isOutputBlocked)
                .count());
        return "audit";
    }
}
