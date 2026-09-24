package com.semantyca.core.model.cnst;

public enum SummaryType {
    BRAND,
    USER,
    /** The internal Ask chat was removed; kept only so existing rows still load. */
    @Deprecated
    ASK
}
