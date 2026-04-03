package com.example.aiopsagent.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.aiopsagent.model.ToolExecutionRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ToolAuditService {

    private static final ThreadLocal<List<ToolExecutionRecord>> EXECUTIONS =
            ThreadLocal.withInitial(ArrayList::new);

    private final ObjectMapper objectMapper;

    public ToolAuditService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void startSession() {
        EXECUTIONS.set(new ArrayList<>());
    }

    public void record(String toolName, Object input, Object output) {
        EXECUTIONS.get().add(new ToolExecutionRecord(
                toolName,
                serialize(input),
                serialize(output),
                Instant.now()
        ));
    }

    public List<ToolExecutionRecord> snapshot() {
        return List.copyOf(EXECUTIONS.get());
    }

    public void clearSession() {
        EXECUTIONS.remove();
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        }
        catch (JsonProcessingException exception) {
            return String.valueOf(value);
        }
    }
}
