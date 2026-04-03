package com.example.aiopsagent.tool;

import java.util.List;
import java.util.Map;

import com.example.aiopsagent.model.IncidentSummary;
import com.example.aiopsagent.model.ServiceStatusReport;
import com.example.aiopsagent.model.ServiceSummary;
import com.example.aiopsagent.service.MockOpsDataService;
import com.example.aiopsagent.service.ToolAuditService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class OperationsToolbox {

    private final MockOpsDataService mockOpsDataService;
    private final ToolAuditService toolAuditService;

    public OperationsToolbox(MockOpsDataService mockOpsDataService, ToolAuditService toolAuditService) {
        this.mockOpsDataService = mockOpsDataService;
        this.toolAuditService = toolAuditService;
    }

    @Tool(description = "List all supported services that can be diagnosed in this demo environment. Use this when the user asks what systems are available or mentions an unknown service.")
    public List<ServiceSummary> listInspectableServices() {
        List<ServiceSummary> result = mockOpsDataService.listServices();
        toolAuditService.record("listInspectableServices", Map.of(), result);
        return result;
    }

    @Tool(description = "Get the latest health status for a supported service, including error rate, p95 latency, suspected cause and the next troubleshooting step. Use this before giving a diagnosis about a specific service.")
    public ServiceStatusReport getServiceStatus(
            @ToolParam(description = "The canonical service name, for example payment-service or inventory-service")
            String serviceName
    ) {
        ServiceStatusReport result = mockOpsDataService.getServiceStatus(serviceName);
        toolAuditService.record("getServiceStatus", Map.of("serviceName", serviceName), result);
        return result;
    }

    @Tool(description = "Get the most recent incident summary for a supported service. Use this when the user asks for historical context, possible root causes, or previous fixes.")
    public IncidentSummary getRecentIncidentSummary(
            @ToolParam(description = "The canonical service name, for example payment-service or inventory-service")
            String serviceName
    ) {
        IncidentSummary result = mockOpsDataService.getRecentIncidentSummary(serviceName);
        toolAuditService.record("getRecentIncidentSummary", Map.of("serviceName", serviceName), result);
        return result;
    }
}
