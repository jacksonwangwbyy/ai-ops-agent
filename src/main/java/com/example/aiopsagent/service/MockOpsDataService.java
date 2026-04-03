package com.example.aiopsagent.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.aiopsagent.model.IncidentSummary;
import com.example.aiopsagent.model.ServiceStatusReport;
import com.example.aiopsagent.model.ServiceSummary;
import org.springframework.stereotype.Service;

@Service
public class MockOpsDataService {

    private final Map<String, ServiceStatusReport> serviceReports;
    private final Map<String, IncidentSummary> incidentSummaries;
    private final Map<String, ServiceSummary> serviceSummaries;

    public MockOpsDataService() {
        this.serviceReports = new LinkedHashMap<>();
        this.incidentSummaries = new LinkedHashMap<>();
        this.serviceSummaries = new LinkedHashMap<>();

        seed();
    }

    public List<ServiceSummary> listServices() {
        return List.copyOf(serviceSummaries.values());
    }

    public ServiceStatusReport getServiceStatus(String serviceName) {
        return serviceReports.getOrDefault(
                normalize(serviceName),
                new ServiceStatusReport(
                        serviceName,
                        "UNKNOWN",
                        0.0,
                        0,
                        "Service is not registered in the demo environment",
                        "Ask the user to provide one of the supported service names before diagnosing further"
                )
        );
    }

    public IncidentSummary getRecentIncidentSummary(String serviceName) {
        return incidentSummaries.getOrDefault(
                normalize(serviceName),
                new IncidentSummary(
                        serviceName,
                        "N/A",
                        "N/A",
                        "No recorded incident exists for this service in the demo environment",
                        "Ask a clarifying question or inspect a supported service instead"
                )
        );
    }

    private void seed() {
        addService(
                new ServiceSummary("payment-service", "DEGRADED", "payments-platform"),
                new ServiceStatusReport(
                        "payment-service",
                        "DEGRADED",
                        7.8,
                        1860,
                        "The downstream fraud-check dependency is timing out and causing request pile-up",
                        "Check the fraud-check dependency latency and temporarily reduce synchronous retry pressure"
                ),
                new IncidentSummary(
                        "payment-service",
                        "INC-2026-0401",
                        "2026-04-01T10:25:00Z",
                        "Elevated timeout rate after a retry policy change increased pressure on the fraud-check service",
                        "Rollback aggressive retries first, then verify dependency recovery before restoring traffic"
                )
        );

        addService(
                new ServiceSummary("order-service", "HEALTHY", "core-commerce"),
                new ServiceStatusReport(
                        "order-service",
                        "HEALTHY",
                        0.3,
                        210,
                        "No major anomaly detected",
                        "Continue monitoring and correlate with upstream callers if users still report failures"
                ),
                new IncidentSummary(
                        "order-service",
                        "INC-2026-0318",
                        "2026-03-18T02:40:00Z",
                        "Short-lived spike caused by database connection pool saturation during batch settlement",
                        "Verify batch windows and connection pool settings before scaling database resources"
                )
        );

        addService(
                new ServiceSummary("inventory-service", "WARNING", "supply-chain"),
                new ServiceStatusReport(
                        "inventory-service",
                        "WARNING",
                        1.9,
                        840,
                        "Kafka consumer lag is increasing and stock update propagation is delayed",
                        "Inspect consumer lag, partition skew and any recent deployment affecting event handling"
                ),
                new IncidentSummary(
                        "inventory-service",
                        "INC-2026-0329",
                        "2026-03-29T16:10:00Z",
                        "Inventory drift occurred after one consumer group lost partition ownership during rebalance",
                        "Check rebalance logs and confirm consumer group stability before replaying missed messages"
                )
        );
    }

    private void addService(
            ServiceSummary serviceSummary,
            ServiceStatusReport report,
            IncidentSummary incidentSummary
    ) {
        String key = normalize(serviceSummary.serviceName());
        serviceSummaries.put(key, serviceSummary);
        serviceReports.put(key, report);
        incidentSummaries.put(key, incidentSummary);
    }

    private String normalize(String serviceName) {
        return serviceName == null ? "" : serviceName.trim().toLowerCase(Locale.ROOT);
    }
}
