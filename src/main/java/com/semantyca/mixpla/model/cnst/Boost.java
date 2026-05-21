package com.semantyca.mixpla.model.cnst;

public enum Boost {
    SUPER_BOOST(1),
    BOOST(1),
    NOTHING(0),
    QUARANTINE(-1);

    private final int value;

    Boost(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Boost fromValue(Integer v) {
        if (v == null) return null;
        for (Boost p : values()) {
            if (p.value == v) return p;
        }
        return null;
    }
}
