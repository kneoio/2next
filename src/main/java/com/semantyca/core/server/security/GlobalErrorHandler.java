package com.semantyca.core.server.security;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalErrorHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @Override
    public void handle(RoutingContext ctx) {
        Throwable failure = ctx.failure();
        int status = ctx.statusCode();

        if (status <= 0) {
            status = failure != null ? 500 : 404;
        }

        String errorId = UUID.randomUUID().toString();

        if (failure != null) {
            LOGGER.error("ErrorId {} | Path {} | Status {}", errorId, ctx.normalizedPath(), status, failure);
        } else {
            LOGGER.warn("ErrorId {} | Path {} | Status {}", errorId, ctx.normalizedPath(), status);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("error", failure != null && failure.getMessage() != null
                ? failure.getMessage()
                : defaultMessage(status));
        errorResponse.put("path", ctx.normalizedPath());
        errorResponse.put("errorId", errorId);

        ctx.response()
                .setStatusCode(status)
                .putHeader("Content-Type", "application/json")
                .end(Json.encode(errorResponse));
    }

    private String defaultMessage(int status) {
        return switch (status) {
            case 404 -> "Resource not found";
            case 403 -> "Access denied";
            case 405 -> "Method not allowed";
            case 413 -> "Payload too large";
            default -> "Unexpected server error";
        };
    }
}