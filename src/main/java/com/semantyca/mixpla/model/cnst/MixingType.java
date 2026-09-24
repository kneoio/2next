package com.semantyca.mixpla.model.cnst;

public enum MixingType {
    INTRO_SONG,
    JINGLE_INTRO_SONG,
    LISTENER_INTRO_SONG,
    LISTENER_SONG,
    INTRO_LISTENER_SONG,
    NOT_MIXED,
    SONG_ONLY,
    SONG_INTRO_SONG,
    FILLER_JINGLE,
    INTRO_SONG_INTRO_SONG,
    @Deprecated
    SONG_CROSSFADE_SONG,
    SONG_CROSSFADE_SONG_VAR_1,
    SONG_CROSSFADE_SONG_VAR_2,
    JINGLE_GENERATED_JINGLE_WITH_BACKGROUND,
    JINGLE_GENERATED_JINGLE,
    INTRO_JINGLE_GENERATED_JINGLE_WITH_BACKGROUND,
    DJ_SONG,
    INTRO_DJ_SONG,
    DJ_JOIN,
    /** The AI DJ speaking on air with no song, e.g. handing the air to a human DJ. Voice in filePaths INTRO_1. */
    DJ_ANNOUNCEMENT;
}
