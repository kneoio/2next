package com.semantyca.core.llm;

import java.util.Optional;

public interface LlmConfig {
    String getAnthropicApiKey();
    Optional<String> getGroqApiKey();
}
