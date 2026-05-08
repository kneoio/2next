package com.semantyca.core.model.cnst;

import lombok.Getter;

@Getter
public enum ArchivedStatus {
    VALID(0),
    ARCHIVED(1),
    HIDDEN(10);


    private final int code;

    ArchivedStatus(int code) {
        this.code = code;
    }

    public static ArchivedStatus fromCode(int code) {
        for (ArchivedStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}