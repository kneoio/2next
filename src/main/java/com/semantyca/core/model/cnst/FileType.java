package com.semantyca.core.model.cnst;

public enum FileType {
    SOUND_FRAGMENT(101),
    OPUS_ENCODED_SOUND_FRAGMENT(102);

    private final int code;

    FileType(int code) {
        this.code = code;
    }

    public static FileType fromCode(int code) {
        for (FileType s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
