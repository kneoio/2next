package com.semantyca.mixpla.dto.queue.command;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CommandDTO(
        String serviceId,
        CommandType type,
        Instant timestamp,
        UUID traceId,
        String command,
        Map<String, Object> payload
) {
    public static CommandDTO of(String serviceId, CommandType type, UUID traceId, String command, Map<String, Object> payload) {
        return new CommandDTO(serviceId, type,  Instant.now(), traceId, command, payload);
    }
}