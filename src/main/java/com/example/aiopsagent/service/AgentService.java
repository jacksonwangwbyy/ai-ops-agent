package com.example.aiopsagent.service;

import java.time.Instant;

import com.example.aiopsagent.model.AgentReply;
import com.example.aiopsagent.tool.OperationsToolbox;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private final ChatClient chatClient;
    private final OperationsToolbox operationsToolbox;
    private final ToolAuditService toolAuditService;

    public AgentService(
            ChatClient opsChatClient,
            OperationsToolbox operationsToolbox,
            ToolAuditService toolAuditService
    ) {
        this.chatClient = opsChatClient;
        this.operationsToolbox = operationsToolbox;
        this.toolAuditService = toolAuditService;
    }

    public AgentReply chat(String message) {
        toolAuditService.startSession();
        try {
            String answer = this.chatClient.prompt()
                    .user(message)
                    .tools(this.operationsToolbox)
                    .call()
                    .content();

            return new AgentReply(
                    answer,
                    toolAuditService.snapshot(),
                    Instant.now()
            );
        }
        finally {
            toolAuditService.clearSession();
        }
    }
}
