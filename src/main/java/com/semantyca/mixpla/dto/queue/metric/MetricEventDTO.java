package com.semantyca.mixpla.dto.queue.metric;

import java.time.Instant;
import java.util.Map;

public record MetricEventDTO(
        String serviceId,
        String brandName,
        MetricEventType type,
        Instant timestamp,
        Map<String, Object> payload
) {
    public static MetricEventDTO of(String serviceId, String brandName, MetricEventType type, Map<String, Object> payload) {
        return new MetricEventDTO(serviceId, brandName, type, Instant.now(), payload);
    }
}