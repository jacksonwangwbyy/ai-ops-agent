package com.example.aiopsagent.controller;

import java.util.List;

import com.example.aiopsagent.model.AgentReply;
import com.example.aiopsagent.model.ChatRequest;
import com.example.aiopsagent.model.ServiceSummary;
import com.example.aiopsagent.service.AgentService;
import com.example.aiopsagent.service.MockOpsDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AgentController {

    private final AgentService agentService;
    private final MockOpsDataService mockOpsDataService;

    public AgentController(AgentService agentService, MockOpsDataService mockOpsDataService) {
        this.agentService = agentService;
        this.mockOpsDataService = mockOpsDataService;
    }

    @PostMapping("/chat")
    public AgentReply chat(@Valid @RequestBody ChatRequest request) {
        return agentService.chat(request.message());
    }

    @GetMapping("/services")
    public List<ServiceSummary> listServices() {
        return mockOpsDataService.listServices();
    }
}
