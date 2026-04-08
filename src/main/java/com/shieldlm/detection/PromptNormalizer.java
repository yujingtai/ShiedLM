package com.shieldlm.detection;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PromptNormalizer {

    public NormalizedPrompt normalize(String prompt) {
        String originalText = prompt == null ? "" : prompt;
        String normalizedText = originalText
                .replace('，', ' ')
                .replace('。', ' ')
                .replace('！', ' ')
                .replace('？', ' ')
                .replace('、', ' ')
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
        String compactText = normalizedText.replace(" ", "");
        List<String> tokens = normalizedText.isBlank()
                ? List.of()
                : Arrays.stream(normalizedText.split(" "))
                .filter(token -> !token.isBlank())
                .toList();
        return new NormalizedPrompt(originalText, normalizedText, compactText, tokens);
    }
}
