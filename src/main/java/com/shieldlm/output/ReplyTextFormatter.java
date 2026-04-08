package com.shieldlm.output;

public class ReplyTextFormatter {

    public String format(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        String formatted = text.replace("\r\n", "\n");
        formatted = formatted.replaceAll("(?m)^#{1,6}\\s*", "");
        formatted = formatted.replace("**", "");
        formatted = formatted.replace("__", "");
        formatted = formatted.replace("`", "");
        formatted = formatted.replaceAll("(?m)^>\\s*", "");
        formatted = formatted.replaceAll("(?m)^\\s*[*+-]\\s+", "- ");
        formatted = formatted.replaceAll("(?m)[ \\t]+$", "");
        formatted = formatted.replaceAll("\\n{3,}", "\n\n");
        return formatted.trim();
    }
}
