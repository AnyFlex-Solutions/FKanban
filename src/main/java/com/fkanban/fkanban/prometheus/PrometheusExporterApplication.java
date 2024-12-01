package com.fkanban.fkanban.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PrometheusExporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrometheusExporterApplication.class, args);
    }

    @RestController
    class MetricsController {

        private final PrometheusMeterRegistry prometheusRegistry;

        MetricsController() {
            this.prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
            MeterRegistry registry = new SimpleMeterRegistry();
            new ProcessorMetrics().bindTo(prometheusRegistry);
        }

        @GetMapping("/metrics")
        public String scrape() {
            return prometheusRegistry.scrape();
        }
    }
}
