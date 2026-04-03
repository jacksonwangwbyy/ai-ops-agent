package com.example.aiopsagent.model;

import java.time.Instant;

public record ToolExecutionRecord(
        String toolName,
        String input,
        String output,
        Instant executedAt
) {
}
