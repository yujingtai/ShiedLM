package com.shieldlm.audit;

import com.shieldlm.core.ShieldResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditRecordRepository auditRecordRepository;

    public AuditLogService(AuditRecordRepository auditRecordRepository) {
        this.auditRecordRepository = auditRecordRepository;
    }

    /**
     * 把一次对话的判定结果写入审计表，后续审计页和统计页都会复用这些数据。
     */
    public void save(ShieldResponse response) {
        String attackTypes = response.decision().hits().stream()
                .map(hit -> hit.attackType().name())
                .distinct()
                .collect(Collectors.joining(", "));

        AuditRecord record = new AuditRecord(
                LocalDateTime.now(),
                response.userPrompt(),
                attackTypes,
                response.decision().riskLevel(),
                response.decision().action(),
                response.outputBlocked(),
                response.finalReply()
        );
        auditRecordRepository.save(record);
    }

    public List<AuditRecord> findRecent() {
        return auditRecordRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
