package com.semantyca.mixpla.model.cnst;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum STTEngineType {
    GOOGLE("google");

    private final String value;

    STTEngineType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static STTEngineType fromValue(String value) {
        for (STTEngineType type : STTEngineType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown STTEngineType: " + value);
    }
}
