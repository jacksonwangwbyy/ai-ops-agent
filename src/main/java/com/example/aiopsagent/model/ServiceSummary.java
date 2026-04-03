package com.example.aiopsagent.model;

public record ServiceSummary(
        String serviceName,
        String status,
        String ownerTeam
) {
}
