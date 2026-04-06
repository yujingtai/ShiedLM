package com.shieldlm.rules;

import com.shieldlm.audit.AuditRecordRepository;
import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final AuditRecordRepository auditRecordRepository;

    public StatisticsService(AuditRecordRepository auditRecordRepository) {
        this.auditRecordRepository = auditRecordRepository;
    }

    /**
     * 统计页只展示最核心的几个指标，保证答辩时一眼能讲清楚：
     * 总请求量、高风险量、输入被拦截量、输出被拦截量。
     */
    public StatisticsSummary getSummary() {
        return new StatisticsSummary(
                auditRecordRepository.count(),
                auditRecordRepository.countByRiskLevel(RiskLevel.HIGH),
                auditRecordRepository.countByDefenseAction(DefenseAction.BLOCK),
                auditRecordRepository.countByOutputBlockedTrue()
        );
    }
}
