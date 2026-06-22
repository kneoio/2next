package com.semantyca.mixpla.dto.queue.command;

import com.semantyca.core.dto.queue.ICommandType;

public enum CommandType implements ICommandType {
    FLOW_RESTART,
    SONG_RATED,
    WAKE_UP_DJ,
    GET_SLEEP_DJ,
    REBUILD_AGENDA,
    BACK_PRESSURE_EMISSION,
    AIVOX_INIT_BRAND
}
