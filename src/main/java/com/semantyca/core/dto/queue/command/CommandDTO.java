package com.semantyca.core.dto.queue.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.semantyca.mixpla.dto.queue.command.CommandType;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

// Plain class, not a record: Jackson's built-in java.lang.Record support probes
// Class.getRecordComponents() unconditionally (even with an explicit @JsonCreator present),
// and that reflection is unreliable under Quarkus dev mode's classloader for a class loaded
// from a separate dependency jar (2next) -- same class of issue as the removed ICommandType
// annotation-based polymorphism. Accessor method names match the record's former implicit
// accessors (serviceId(), type(), ...) so callers across services didn't need to change.
public final class CommandDTO {
    private final String serviceId;
    private final CommandType type;
    private final Instant timestamp;
    private final UUID traceId;
    @Deprecated
    private final String command;
    private final Map<String, Object> payload;

    @JsonCreator
    public CommandDTO(
            @JsonProperty("serviceId") String serviceId,
            @JsonProperty("type") CommandType type,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("traceId") UUID traceId,
            @JsonProperty("command") String command,
            @JsonProperty("payload") Map<String, Object> payload
    ) {
        this.serviceId = serviceId;
        this.type = type;
        this.timestamp = timestamp;
        this.traceId = traceId;
        this.command = command;
        this.payload = payload;
    }

    public static CommandDTO of(String serviceId, CommandType type, UUID traceId, String command, Map<String, Object> payload) {
        return new CommandDTO(serviceId, type, Instant.now(), traceId, command, payload);
    }

    @JsonProperty("serviceId")
    public String serviceId() {
        return serviceId;
    }

    @JsonProperty("type")
    public CommandType type() {
        return type;
    }

    @JsonProperty("timestamp")
    public Instant timestamp() {
        return timestamp;
    }

    @JsonProperty("traceId")
    public UUID traceId() {
        return traceId;
    }

    @Deprecated
    @JsonProperty("command")
    public String command() {
        return command;
    }

    @JsonProperty("payload")
    public Map<String, Object> payload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandDTO that)) return false;
        return Objects.equals(serviceId, that.serviceId) && type == that.type
                && Objects.equals(timestamp, that.timestamp) && Objects.equals(traceId, that.traceId)
                && Objects.equals(command, that.command) && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, type, timestamp, traceId, command, payload);
    }

    @Override
    public String toString() {
        return "CommandDTO[serviceId=" + serviceId + ", type=" + type + ", timestamp=" + timestamp
                + ", traceId=" + traceId + ", command=" + command + ", payload=" + payload + "]";
    }
}
