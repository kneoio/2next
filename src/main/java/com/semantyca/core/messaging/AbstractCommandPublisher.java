package com.semantyca.core.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.semantyca.core.dto.queue.command.CommandDTO;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

public abstract class AbstractCommandPublisher {

    private static final Logger LOGGER = Logger.getLogger(AbstractCommandPublisher.class);
    protected static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    protected abstract Emitter<byte[]> getEmitter();

    protected void publishEvent(CommandDTO event) {
        Uni.createFrom().item(() -> {
                    try {
                        return objectMapper.writeValueAsBytes(event);
                    } catch (Exception e) {
                        LOGGER.errorf(e, "Failed to serialize command event type=%s", event.type());
                        throw new RuntimeException(e);
                    }
                })
                .invoke(bytes -> getEmitter().send(bytes))
                .onFailure().invoke(e -> LOGGER.errorf(e, "Failed to publish command event type=%s", event.type()))
                .onItem().ignore().andContinueWithNull()
                .subscribe().with(
                        v -> LOGGER.infof("Command published OK type=%s traceId=%s", event.type(), event.traceId()),
                        e -> LOGGER.errorf(e, "Failed to publish command type=%s traceId=%s", event.type(), event.traceId())
                );
    }
}
