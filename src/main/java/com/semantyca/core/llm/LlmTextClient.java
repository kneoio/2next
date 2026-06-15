package com.semantyca.core.llm;

import io.smallrye.mutiny.Uni;

public interface LlmTextClient {
    Uni<LlmTextResult> createTextMessage(String apiKey, String model, long maxTokens, String systemPrompt, String userMessage);
}
