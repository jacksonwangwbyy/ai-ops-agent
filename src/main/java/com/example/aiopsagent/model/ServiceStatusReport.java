package com.example.aiopsagent.model;

public record ServiceStatusReport(
        String serviceName,
        String status,
        double errorRatePercent,
        int p95LatencyMs,
        String suspectedCause,
        String suggestedNextStep
) {
}
