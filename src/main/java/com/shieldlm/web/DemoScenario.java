package com.shieldlm.web;

public record DemoScenario(
        String title,
        String category,
        String expectedRisk,
        String expectedAction,
        String summary,
        String prompt
) {
}
