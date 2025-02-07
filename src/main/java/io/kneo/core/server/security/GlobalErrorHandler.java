package io.kneo.core.server.security;

import io.kneo.core.repository.exception.DocumentHasNotFoundException;
import io.kneo.core.repository.exception.DocumentModificationAccessException;
import io.kneo.core.repository.exception.UserNotFoundException;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.Map;
import java.util.NoSuchElementException;

public class GlobalErrorHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String JSON_TYPE = "application/json";

    private final Map<Class<? extends Throwable>, ErrorResponse> errorMappings = Map.of(
            IllegalArgumentException.class, new ErrorResponse(ErrorResponse.ErrorCode.INVALID_REQUEST),
            DocumentHasNotFoundException.class, new ErrorResponse(ErrorResponse.ErrorCode.DOCUMENT_NOT_FOUND),
            UserNotFoundException.class, new ErrorResponse(ErrorResponse.ErrorCode.USER_NOT_FOUND),
            DocumentModificationAccessException.class, new ErrorResponse(ErrorResponse.ErrorCode.DOCUMENT_ACCESS_DENIED),
            ConnectException.class, new ErrorResponse(ErrorResponse.ErrorCode.CONNECTION_ERROR),
            PgException.class, new ErrorResponse(ErrorResponse.ErrorCode.DATABASE_ERROR),
            NoSuchElementException.class, new ErrorResponse(ErrorResponse.ErrorCode.RESOURCE_NOT_AVAILABLE)
    );

    @Override
    public void handle(RoutingContext context) {
        Throwable failure = context.failure();
        Throwable rootCause = getRootCause(failure);

        ErrorResponse response = errorMappings.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(rootCause))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new ErrorResponse(ErrorResponse.ErrorCode.UNKNOWN_ERROR));

        // Log all errors with appropriate levels
        if (response.isLogError()) {
            LOGGER.error("Global error handler - Critical error: ", failure);
        } else {
            // Log client errors at warning level
            if (response.getStatus() >= 400 && response.getStatus() < 500) {
                LOGGER.warn("Global error handler - Client error: {} - {}",
                        response.getCode(),
                        failure.getMessage(),
                        failure);
            } else {
                // Log other errors at error level
                LOGGER.error("Global error handler - Server error: {} - {}",
                        response.getCode(),
                        failure.getMessage(),
                        failure);
            }
        }

        sendErrorResponse(context, response);
    }

    private Throwable getRootCause(Throwable throwable) {
        return throwable.getCause() != null ? getRootCause(throwable.getCause()) : throwable;
    }

    private void sendErrorResponse(RoutingContext context, ErrorResponse response) {
        var errorResponse = Map.of(
                "status", response.getStatus(),
                "error", Map.of(
                        "code", response.getCode(),
                        "message", response.getMessage(),
                        "details", response.getDetails(),
                        "timestamp", System.currentTimeMillis(),
                        "path", context.request().uri(),
                        "method", context.request().method().toString(),
                        "trace", context.failure() != null ? context.failure().getMessage() : "No error trace available"
                )
        );

        context.response()
                .setStatusCode(response.getStatus())
                .putHeader(CONTENT_TYPE, JSON_TYPE)
                .end(Json.encode(errorResponse));
    }
}