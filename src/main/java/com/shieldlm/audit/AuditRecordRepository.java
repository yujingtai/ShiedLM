package com.shieldlm.audit;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {

    long countByRiskLevel(RiskLevel riskLevel);

    long countByDefenseAction(DefenseAction defenseAction);

    long countByOutputBlockedTrue();
}
