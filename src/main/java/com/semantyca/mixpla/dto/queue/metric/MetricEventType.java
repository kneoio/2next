package com.semantyca.mixpla.dto.queue.metric;

public enum MetricEventType {
    @Deprecated
    SONG_SENT,
    @Deprecated
    INTRO_GENERATED,
    @Deprecated
    INTRO_SKIPPED,
    @Deprecated
    SCENE_EXHAUSTED,
    @Deprecated
    TTS_FAILED,
    @Deprecated
    QUEUE_MESSAGE_SENT,
    @Deprecated
    AI_TOKENS_USED,
    @Deprecated
    MESSAGE_RECEIVED,

    FATAL_ERROR,
    ERROR,
    COMMAND,
    INFORMATION,
    IMPORTANT_INFORMATION,
    WARNING
}
