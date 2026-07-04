package com.semantyca.core.dto.queue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.semantyca.mixpla.dto.queue.command.CommandType;

@JsonDeserialize(as = CommandType.class)
public interface ICommandType {
    String name();
}
