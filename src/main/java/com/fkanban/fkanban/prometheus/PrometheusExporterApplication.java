package com.fkanban.fkanban.prometheus;

import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.DiskSpaceMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@SpringBootApplication
public class PrometheusExporterApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrometheusExporterApplication.class, args);
    }

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Configuration
    public static class MetricsConfiguration {

        private final PrometheusMeterRegistry registry;

        public MetricsConfiguration(PrometheusMeterRegistry registry) {
            this.registry = registry;
            bindMetrics();
        }

        private void bindMetrics() {
            new ProcessorMetrics().bindTo(registry); // Метрики процессора
            new JvmMemoryMetrics().bindTo(registry); // Память JVM
            new JvmThreadMetrics().bindTo(registry); // Потоки JVM
            new ClassLoaderMetrics().bindTo(registry); // Метрики загрузчика классов
            new DiskSpaceMetrics(new File(".")).bindTo(registry); // Диск
        }
    }

    @RestController
    class MetricsController {
        private final PrometheusMeterRegistry prometheusRegistry;

        MetricsController(PrometheusMeterRegistry prometheusRegistry) {
            this.prometheusRegistry = prometheusRegistry;
        }

        @GetMapping("/metrics")
        public String scrape() {
            return prometheusRegistry.scrape();
        }
    }
}
