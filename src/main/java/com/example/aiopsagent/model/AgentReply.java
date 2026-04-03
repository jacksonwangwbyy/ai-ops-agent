package com.example.aiopsagent.model;

import java.time.Instant;
import java.util.List;

public record AgentReply(
        String answer,
        List<ToolExecutionRecord> toolExecutions,
        Instant createdAt
) {
}
