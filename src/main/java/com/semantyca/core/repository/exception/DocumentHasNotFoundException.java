package com.semantyca.core.repository.exception;


import java.util.UUID;

public class DocumentHasNotFoundException extends RuntimeException  {

    /** Server-log diagnostics only — never part of the message, so it never reaches the client response. */
    private final Long userId;

    public DocumentHasNotFoundException(String id) {
        super(id);
        this.userId = null;
    }

    public DocumentHasNotFoundException(UUID id) {
        super(id.toString());
        this.userId = null;
    }

    public DocumentHasNotFoundException(UUID id, Long userId) {
        super(id.toString());
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
