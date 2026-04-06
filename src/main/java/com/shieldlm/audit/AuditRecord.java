package com.shieldlm.audit;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private String userPrompt;
    private String attackTypes;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    private DefenseAction defenseAction;

    private boolean outputBlocked;
    private String finalReply;

    protected AuditRecord() {
    }

    public AuditRecord(
            LocalDateTime createdAt,
            String userPrompt,
            String attackTypes,
            RiskLevel riskLevel,
            DefenseAction defenseAction,
            boolean outputBlocked,
            String finalReply
    ) {
        this.createdAt = createdAt;
        this.userPrompt = userPrompt;
        this.attackTypes = attackTypes;
        this.riskLevel = riskLevel;
        this.defenseAction = defenseAction;
        this.outputBlocked = outputBlocked;
        this.finalReply = finalReply;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public String getAttackTypes() {
        return attackTypes;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public DefenseAction getDefenseAction() {
        return defenseAction;
    }

    public boolean isOutputBlocked() {
        return outputBlocked;
    }

    public String getFinalReply() {
        return finalReply;
    }
}
