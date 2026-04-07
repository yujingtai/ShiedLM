package com.shieldlm.audit;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuditController.class)
class AuditControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    @Test
    void rendersAuditPageWithRecentRecords() throws Exception {
        List<AuditRecord> records = List.of(
                new AuditRecord(
                        LocalDateTime.of(2026, 4, 6, 20, 30),
                        "attack prompt",
                        "PRIVILEGE_OVERRIDE, PROMPT_EXTRACTION",
                        RiskLevel.HIGH,
                        DefenseAction.BLOCK,
                        false,
                        "request blocked"
                )
        );
        Page<AuditRecord> recordPage = new PageImpl<>(records, PageRequest.of(0, 5), 1);
        when(auditLogService.findRecent(null, null, null, 0)).thenReturn(recordPage);

        mockMvc.perform(get("/audit"))
                .andExpect(status().isOk())
                .andExpect(view().name("audit"))
                .andExpect(model().attribute("records", records))
                .andExpect(model().attribute("riskLevels", hasSize(RiskLevel.values().length)))
                .andExpect(model().attribute("defenseActions", hasSize(DefenseAction.values().length)))
                .andExpect(model().attribute("displayedCount", 1L))
                .andExpect(model().attribute("totalMatches", 1L))
                .andExpect(model().attribute("pageNumber", 0))
                .andExpect(model().attribute("pageNumberDisplay", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("blockedCount", 1L))
                .andExpect(model().attribute("outputBlockedCount", 0L))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Audit")));
    }

    @Test
    void filtersAuditPageByRiskLevelAndDefenseAction() throws Exception {
        List<AuditRecord> records = List.of(
                new AuditRecord(
                        LocalDateTime.of(2026, 4, 6, 20, 30),
                        "attack prompt",
                        "PROMPT_EXTRACTION",
                        RiskLevel.HIGH,
                        DefenseAction.BLOCK,
                        false,
                        "blocked"
                )
        );
        Page<AuditRecord> recordPage = new PageImpl<>(records, PageRequest.of(0, 5), 1);
        when(auditLogService.findRecent(null, RiskLevel.HIGH, DefenseAction.BLOCK, 0)).thenReturn(recordPage);

        mockMvc.perform(get("/audit")
                        .param("riskLevel", "HIGH")
                        .param("defenseAction", "BLOCK"))
                .andExpect(status().isOk())
                .andExpect(view().name("audit"))
                .andExpect(model().attribute("records", records))
                .andExpect(model().attribute("selectedRiskLevel", RiskLevel.HIGH))
                .andExpect(model().attribute("selectedDefenseAction", DefenseAction.BLOCK))
                .andExpect(model().attribute("displayedCount", 1L))
                .andExpect(model().attribute("blockedCount", 1L));

        verify(auditLogService).findRecent(null, RiskLevel.HIGH, DefenseAction.BLOCK, 0);
    }

    @Test
    void searchesAuditPageWithKeywordAndPagination() throws Exception {
        AuditRecord record = new AuditRecord(
                LocalDateTime.of(2026, 4, 6, 20, 30),
                "prompt attack",
                "PROMPT_EXTRACTION",
                RiskLevel.HIGH,
                DefenseAction.BLOCK,
                false,
                "blocked"
        );
        Page<AuditRecord> recordPage = new PageImpl<>(List.of(record), PageRequest.of(1, 5), 8);
        when(auditLogService.findRecent("prompt", RiskLevel.HIGH, DefenseAction.BLOCK, 1)).thenReturn(recordPage);

        mockMvc.perform(get("/audit")
                        .param("keyword", "prompt")
                        .param("riskLevel", "HIGH")
                        .param("defenseAction", "BLOCK")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("audit"))
                .andExpect(model().attribute("records", List.of(record)))
                .andExpect(model().attribute("keyword", "prompt"))
                .andExpect(model().attribute("selectedRiskLevel", RiskLevel.HIGH))
                .andExpect(model().attribute("selectedDefenseAction", DefenseAction.BLOCK))
                .andExpect(model().attribute("pageNumber", 1))
                .andExpect(model().attribute("pageNumberDisplay", 2))
                .andExpect(model().attribute("totalPages", 2))
                .andExpect(model().attribute("totalMatches", 6L))
                .andExpect(model().attribute("hasPrevious", true))
                .andExpect(model().attribute("hasNext", false));

        verify(auditLogService).findRecent("prompt", RiskLevel.HIGH, DefenseAction.BLOCK, 1);
    }
}
