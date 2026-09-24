package com.semantyca.core.model.cnst;

public enum ChatType {
    PUBLIC,
    OWNER,
    /** The internal Ask chat was removed; kept only so existing rows still load. */
    @Deprecated
    ASK
}
