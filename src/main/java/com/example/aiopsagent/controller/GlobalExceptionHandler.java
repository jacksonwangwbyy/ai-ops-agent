package com.example.aiopsagent.controller;

import java.time.Instant;

import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NonTransientAiException.class)
    public ResponseEntity<ApiErrorResponse> handleAiProviderError(NonTransientAiException exception) {
        String message = exception.getMessage();

        if (message != null && message.contains("Insufficient Balance")) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiErrorResponse(
                            "AI_PROVIDER_BALANCE_EXHAUSTED",
                            "The configured AI provider rejected the request because the account balance is insufficient.",
                            Instant.now()
                    ));
        }

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiErrorResponse(
                        "AI_PROVIDER_ERROR",
                        message == null ? "The AI provider returned an error." : message,
                        Instant.now()
                ));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleNetworkError(ResourceAccessException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiErrorResponse(
                        "AI_PROVIDER_NETWORK_ERROR",
                        "The application could not reach the configured AI provider.",
                        Instant.now()
                ));
    }
}
