package com.fkanban.fkanban.prometheus;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class SystemMetricsProvider {

    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    // Использование процессора
    public double getCpuLoad() {
        return osBean.getSystemCpuLoad(); // Значение от 0.0 до 1.0
    }

    // Свободная физическая память
    public long getFreePhysicalMemory() {
        return osBean.getFreePhysicalMemorySize();
    }

    // Общая физическая память
    public long getTotalPhysicalMemory() {
        return osBean.getTotalPhysicalMemorySize();
    }

    // Используемая физическая память
    public long getUsedPhysicalMemory() {
        return getTotalPhysicalMemory() - getFreePhysicalMemory();
    }
}
