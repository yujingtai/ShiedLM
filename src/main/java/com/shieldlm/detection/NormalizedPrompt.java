package com.shieldlm.detection;

import java.util.List;

public record NormalizedPrompt(
        String originalText,
        String normalizedText,
        String compactText,
        List<String> tokens
) {
}
