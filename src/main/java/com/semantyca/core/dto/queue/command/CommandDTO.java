package com.semantyca.core.dto.queue.command;

import com.semantyca.core.dto.queue.ICommandType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CommandDTO(
        String serviceId,
        String type,
        Instant timestamp,
        UUID traceId,
        @Deprecated
        String command,
        Map<String, Object> payload
) {
    public static CommandDTO of(String serviceId, ICommandType type, UUID traceId, String command, Map<String, Object> payload) {
        return new CommandDTO(serviceId, type.name(), Instant.now(), traceId, command, payload);
    }
}
