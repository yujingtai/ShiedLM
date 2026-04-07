package com.shieldlm.audit;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {

    long countByRiskLevel(RiskLevel riskLevel);

    long countByDefenseAction(DefenseAction defenseAction);

    long countByOutputBlockedTrue();

    @Query("""
            select r from AuditRecord r
            where (:riskLevel is null or r.riskLevel = :riskLevel)
              and (:defenseAction is null or r.defenseAction = :defenseAction)
              and (:keyword is null
                   or lower(coalesce(r.userPrompt, '')) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(r.attackTypes, '')) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(r.finalReply, '')) like lower(concat('%', :keyword, '%')))
            """)
    Page<AuditRecord> search(
            @Param("keyword") String keyword,
            @Param("riskLevel") RiskLevel riskLevel,
            @Param("defenseAction") DefenseAction defenseAction,
            Pageable pageable
    );
}
