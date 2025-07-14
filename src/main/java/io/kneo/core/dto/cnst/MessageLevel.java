package io.kneo.core.dto.cnst;

import lombok.Getter;

@Getter
public enum MessageLevel {
    SUCCESS("success"), MISFORTUNE("misfortune"), FAILURE("failure");

    private final String alias;

    MessageLevel(String alias) {
        this.alias = alias;
    }

}
