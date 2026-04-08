package com.shieldlm.web;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Component("viewTexts")
public class ViewTextHelper {

    public String riskLevel(Object value) {
        return switch (normalize(value)) {
            case "LOW" -> "低风险";
            case "MEDIUM" -> "中风险";
            case "HIGH" -> "高风险";
            default -> fallback(value);
        };
    }

    public String defenseAction(Object value) {
        return switch (normalize(value)) {
            case "ALLOW" -> "直接放行";
            case "REWRITE" -> "改写后放行";
            case "BLOCK" -> "输入拦截";
            case "OUTPUT_BLOCK", "OUTPUT CHECK" -> "输出拦截";
            default -> fallback(value);
        };
    }

    public String auditOutcome(Object defenseAction, boolean outputBlocked) {
        if (outputBlocked) {
            return "放行但输出拦截";
        }
        return switch (normalize(defenseAction)) {
            case "ALLOW" -> "直接放行";
            case "REWRITE" -> "改写后放行";
            case "BLOCK" -> "输入拦截";
            case "OUTPUT_BLOCK", "OUTPUT CHECK" -> "输出拦截";
            default -> fallback(defenseAction);
        };
    }

    public String attackType(Object value) {
        return switch (normalize(value)) {
            case "PRIVILEGE_OVERRIDE" -> "越权覆盖";
            case "PROMPT_EXTRACTION" -> "提示词窃取";
            case "SENSITIVE_DATA_INDUCTION" -> "敏感信息索取";
            case "POLICY_EVASION" -> "策略规避";
            case "ILLEGAL_HARMFUL_CONTENT" -> "违法危险内容";
            case "NORMAL" -> "正常请求";
            default -> fallback(value);
        };
    }

    public String attackTypes(String value) {
        if (value == null || value.isBlank()) {
            return "未命中";
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(this::attackType)
                .collect(Collectors.joining("、"));
    }

    public String ruleKey(String value) {
        String normalized = normalize(value);
        if (normalized.startsWith("SEMANTIC_")) {
            return semanticRuleKey(normalized);
        }
        return switch (normalized) {
            case "IGNORE_PREVIOUS_RULES", "ROLE_OVERRIDE" -> "越权覆盖检测";
            case "SYSTEM_PROMPT_LEAK", "PROMPT_THEFT" -> "提示词窃取检测";
            case "INTERNAL_CONFIG", "SECRET_EXTRACTION" -> "敏感信息索取检测";
            case "UNSAFE_CONTENT_REQUEST", "SEXUAL_ILLEGAL_CONTENT" -> "违法色情内容检测";
            case "VIOLENT_CRIME_GUIDANCE" -> "暴力犯罪内容检测";
            case "DRUG_CRIME_GUIDANCE" -> "制毒贩毒内容检测";
            case "WEAPON_EXPLOSIVE_GUIDANCE" -> "枪爆违禁内容检测";
            case "CYBER_CRIME_GUIDANCE" -> "网络犯罪内容检测";
            case "FRAUD_THEFT_GUIDANCE" -> "诈骗盗窃内容检测";
            default -> fallback(value);
        };
    }

    private String semanticRuleKey(String normalizedValue) {
        String attackTypeText = attackType(normalizedValue.substring("SEMANTIC_".length()));
        if (attackTypeText.equals(normalizedValue.substring("SEMANTIC_".length()))) {
            return "语义裁决";
        }
        return "语义裁决：" + attackTypeText;
    }

    public String riskSignal(Object value) {
        return switch (normalize(value)) {
            case "OVERRIDE_INTENT" -> "越权指令";
            case "PROMPT_LEAK_INTENT" -> "提示词泄露意图";
            case "SECRET_EXTRACTION_INTENT" -> "敏感信息索取";
            case "UNSAFE_CONTENT_INTENT" -> "违法色情请求";
            case "ROLE_HIJACK_INTENT" -> "角色劫持";
            case "VIOLENT_CRIME_INTENT" -> "暴力犯罪意图";
            case "DRUG_CRIME_INTENT" -> "制毒贩毒意图";
            case "WEAPON_EXPLOSIVE_INTENT" -> "枪爆违禁意图";
            case "CYBER_CRIME_INTENT" -> "网络犯罪意图";
            case "FRAUD_THEFT_INTENT" -> "诈骗盗窃意图";
            case "OUTPUT_FORMAT_PROBE" -> "输出格式探测";
            default -> fallback(value);
        };
    }

    public String riskSignals(String value) {
        if (value == null || value.isBlank()) {
            return "未命中";
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(this::riskSignal)
                .collect(Collectors.joining("、"));
    }

    public String outputRiskType(String value) {
        return switch (normalize(value)) {
            case "NONE" -> "无";
            case "SECRET_LEAK" -> "敏感信息泄露";
            case "PROMPT_LEAK" -> "提示词泄露";
            case "UNSAFE_CONTENT" -> "不安全内容";
            default -> fallback(value);
        };
    }

    public String scenarioCategory(String value) {
        return switch (normalize(value)) {
            case "BASELINE" -> "正常业务";
            case "PRIVILEGE OVERRIDE" -> "规则绕过";
            case "HYBRID INJECTION" -> "混合攻击";
            case "OUTPUT GUARD" -> "输出兜底";
            default -> fallback(value);
        };
    }

    private String normalize(Object value) {
        return fallback(value).toUpperCase(Locale.ROOT);
    }

    private String fallback(Object value) {
        return value == null ? "" : value.toString();
    }
}
