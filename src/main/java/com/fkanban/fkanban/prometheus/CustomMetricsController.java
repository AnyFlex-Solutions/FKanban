package com.fkanban.fkanban.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomMetricsController {

    private final MeterRegistry meterRegistry;

    public CustomMetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        meterRegistry.gauge("custom_cpu_usage", this, CustomMetricsController::getCpuUsage);
    }

    private double getCpuUsage() {
        return Math.random();
    }
}