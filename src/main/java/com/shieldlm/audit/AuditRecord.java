package com.shieldlm.audit;

import com.shieldlm.core.model.DefenseAction;
import com.shieldlm.core.model.RiskLevel;
import jakarta.persistence.Column;
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

    @Column(length = 4000)
    private String userPrompt;

    @Column(length = 4000)
    private String attackTypes;

    @Column(length = 4000)
    private String riskSignals;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    private DefenseAction defenseAction;

    private boolean outputBlocked;

    @Column(length = 100)
    private String outputRiskType;

    @Column(length = 4000)
    private String finalReply;

    protected AuditRecord() {
    }

    public AuditRecord(
            LocalDateTime createdAt,
            String userPrompt,
            String attackTypes,
            String riskSignals,
            RiskLevel riskLevel,
            DefenseAction defenseAction,
            boolean outputBlocked,
            String outputRiskType,
            String finalReply
    ) {
        this.createdAt = createdAt;
        this.userPrompt = userPrompt;
        this.attackTypes = attackTypes;
        this.riskSignals = riskSignals;
        this.riskLevel = riskLevel;
        this.defenseAction = defenseAction;
        this.outputBlocked = outputBlocked;
        this.outputRiskType = outputRiskType;
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

    public String getRiskSignals() {
        return riskSignals;
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

    public String getOutputRiskType() {
        return outputRiskType;
    }

    public String getFinalReply() {
        return finalReply;
    }
}
