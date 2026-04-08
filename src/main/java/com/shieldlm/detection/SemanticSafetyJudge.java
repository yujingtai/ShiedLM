package com.shieldlm.detection;

@FunctionalInterface
public interface SemanticSafetyJudge {

    SemanticSafetyVerdict judge(String prompt);

    static SemanticSafetyJudge noop() {
        return prompt -> SemanticSafetyVerdict.none();
    }
}
