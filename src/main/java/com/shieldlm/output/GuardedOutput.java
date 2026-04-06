package com.shieldlm.output;

public record GuardedOutput(String rawReply, String safeReply, boolean blocked) {
}
