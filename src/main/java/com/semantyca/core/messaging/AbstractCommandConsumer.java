package com.semantyca.core.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.semantyca.core.dto.queue.command.CommandDTO;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

public abstract class AbstractCommandConsumer {

    private static final Logger LOGGER = Logger.getLogger(AbstractCommandConsumer.class);
    protected static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    protected abstract Uni<Void> handleCommand(CommandDTO dto);

    protected Uni<Void> processMessage(Message<byte[]> message) {
        byte[] payload = message.getPayload();
        return Uni.createFrom().item(() -> {
                    try {
                        return objectMapper.readValue(payload, CommandDTO.class);
                    } catch (Exception e) {
                        LOGGER.error("Failed to deserialize CommandDTO", e);
                        throw new RuntimeException(e);
                    }
                })
                .chain(dto -> {
                    LOGGER.debugf("Received command: type=%s command=%s", dto.type(), dto.command());
                    return handleCommand(dto);
                })
                .onItem().transformToUni(v -> Uni.createFrom().completionStage(message.ack()))
                .onFailure().recoverWithUni(e -> {
                    LOGGER.errorf("Failed processing command message: %s", e.getMessage());
                    return Uni.createFrom().completionStage(message.nack(e));
                });
    }
}
