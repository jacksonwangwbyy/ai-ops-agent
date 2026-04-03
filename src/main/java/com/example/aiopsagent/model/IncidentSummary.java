package com.example.aiopsagent.model;

public record IncidentSummary(
        String serviceName,
        String incidentId,
        String startedAt,
        String summary,
        String resolutionHint
) {
}
