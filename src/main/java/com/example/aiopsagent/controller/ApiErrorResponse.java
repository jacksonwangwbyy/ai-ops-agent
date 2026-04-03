package com.example.aiopsagent.controller;

import java.time.Instant;

public record ApiErrorResponse(
        String error,
        String message,
        Instant timestamp
) {
}
