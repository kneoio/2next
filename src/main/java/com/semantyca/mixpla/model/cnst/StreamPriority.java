package com.semantyca.mixpla.model.cnst;

import lombok.Getter;

@Getter
public enum StreamPriority {
    HARD_INTERRUPT(6),
    GENTLE_INTERRUPT(7),
    PRIORITIZED_FRONT(8),
    PRIORITIZED(9),
    NORMAL(10),
    OPTIONAL(11);

    private final int value;

    StreamPriority(int value) {
        this.value = value;
    }

    public static StreamPriority fromValue(int value) {
        for (StreamPriority p : values()) {
            if (p.value == value) return p;
        }
        return NORMAL;
    }
}
