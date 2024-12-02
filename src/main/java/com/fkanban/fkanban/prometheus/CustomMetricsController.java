package com.fkanban.fkanban.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomMetricsController {

    private final SystemMetricsProvider systemMetricsProvider;
    private final MeterRegistry meterRegistry;

    public CustomMetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.systemMetricsProvider = new SystemMetricsProvider();

        // Регистрируем кастомные метрики
        meterRegistry.gauge("system_cpu_load", systemMetricsProvider, SystemMetricsProvider::getCpuLoad);
        meterRegistry.gauge("system_memory_free", systemMetricsProvider, SystemMetricsProvider::getFreePhysicalMemory);
        meterRegistry.gauge("system_memory_used", systemMetricsProvider, SystemMetricsProvider::getUsedPhysicalMemory);
    }

    @GetMapping("/metrics/custom")
    public String customMetrics() {
        return "Custom metrics are being recorded!";
    }
}