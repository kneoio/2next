package com.semantyca.mixpla.model.cnst;

import lombok.Getter;

@Getter
public enum SearchEngineType {
    PERPLEXITY("perplexity"),
    BRAVE("brave");

    private final String value;

    SearchEngineType(String value) {
        this.value = value;
    }

}
