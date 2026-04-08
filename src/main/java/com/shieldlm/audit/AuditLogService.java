package com.shieldlm.audit;

import com.shieldlm.core.ShieldResponse;
import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private static final int PAGE_SIZE = 5;

    private final AuditRecordRepository auditRecordRepository;

    public AuditLogService(AuditRecordRepository auditRecordRepository) {
        this.auditRecordRepository = auditRecordRepository;
    }

    public void save(ShieldResponse response) {
        String attackTypes = response.decision().hits().stream()
                .map(hit -> hit.attackType().name())
                .distinct()
                .collect(Collectors.joining(", "));
        String riskSignals = response.decision().hits().stream()
                .flatMap(hit -> hit.signals().stream())
                .map(Enum::name)
                .distinct()
                .collect(Collectors.joining(", "));

        AuditRecord record = new AuditRecord(
                LocalDateTime.now(),
                response.userPrompt(),
                attackTypes,
                riskSignals,
                response.decision().riskLevel(),
                response.decision().action(),
                response.outputBlocked(),
                response.outputRiskType().name(),
                response.finalReply()
        );
        auditRecordRepository.save(record);
    }

    public Page<AuditRecord> findRecent(String keyword, RiskLevel riskLevel, DefenseAction defenseAction, int page) {
        String normalizedKeyword = normalizeKeyword(keyword);
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return auditRecordRepository.search(normalizedKeyword, riskLevel, defenseAction, pageable);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
