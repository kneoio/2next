package com.semantyca.core.config;

import io.smallrye.config.WithDefault;

public interface LlmConfig {

    @WithDefault("dummy")
    String anthropicApiKey();

    @WithDefault("dummy")
    String groqApiKey();
}
