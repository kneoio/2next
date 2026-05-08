package com.semantyca.core.model.cnst;

import lombok.Getter;

@Getter
public enum LifecycleStatus {
    NOT_APPROVED(11),
    APPROVED(12),
    REJECTED(13);

    private final int code;

    LifecycleStatus(int code) {
        this.code = code;
    }

    public static LifecycleStatus fromCode(int code) {
        for (LifecycleStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}