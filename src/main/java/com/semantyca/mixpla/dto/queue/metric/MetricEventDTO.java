package com.semantyca.mixpla.dto.queue.metric;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record MetricEventDTO(
        String serviceId,
        String brandName,
        MetricEventType type,
        Instant timestamp,
        UUID traceId,
        String code,
        Map<String, Object> payload
) {
    public static MetricEventDTO of(String serviceId, String brandName, MetricEventType type, UUID traceId,  String code, Map<String, Object> payload) {
        return new MetricEventDTO(serviceId, brandName, type, Instant.now(), traceId, code, payload);
    }
}