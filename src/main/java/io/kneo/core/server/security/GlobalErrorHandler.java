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
            IllegalArgumentException.class, new ErrorResponse(ErrorResponse.ErrorCode.INVALID_REQUEST, "Unauthorized"),
            DocumentHasNotFoundException.class, new ErrorResponse(ErrorResponse.ErrorCode.DOCUMENT_NOT_FOUND, "Unauthorized"),
            UserNotFoundException.class, new ErrorResponse(ErrorResponse.ErrorCode.USER_NOT_FOUND, "Unauthorized"),
            DocumentModificationAccessException.class, new ErrorResponse(ErrorResponse.ErrorCode.DOCUMENT_ACCESS_DENIED, "Unauthorized"),
            ConnectException.class, new ErrorResponse(ErrorResponse.ErrorCode.CONNECTION_ERROR, "Unauthorized"),
            PgException.class, new ErrorResponse(ErrorResponse.ErrorCode.DATABASE_ERROR, "Unauthorized"),
            NoSuchElementException.class, new ErrorResponse(ErrorResponse.ErrorCode.RESOURCE_NOT_AVAILABLE, "Unauthorized")
    );

    public void handle(RoutingContext context) {
        Throwable failure = context.failure();
        int statusCode = context.statusCode();
        String userName = "undefined";
        if (context.user() != null && context.user().containsKey("username")) {
            userName = context.user().attributes().getMap().get("username").toString();
        }

        String contentLength = context.request().getHeader("Content-Length");
        String contentType = context.request().getHeader("Content-Type");
        String requestPath = context.request().uri();
        String method = context.request().method().toString();
        LOGGER.info("GlobalErrorHandler triggered - Method: {}, Path: {}, Status: {}, Content-Length: {}, Content-Type: {}, User: {}", new Object[]{method, requestPath, statusCode, contentLength, contentType, userName});

        if (failure == null) {
            if (statusCode != -1) {
                LOGGER.warn("HTTP error detected - Status: {}, Path: {}, Content-Length: {}", new Object[]{statusCode, requestPath, contentLength});
                if (statusCode == 413) {
                    LOGGER.error("File upload rejected - Payload too large. Status: 413, Path: {}, Content-Length: {}", requestPath, contentLength);
                    this.sendErrorResponse(context, new ErrorResponse(ErrorResponse.ErrorCode.INVALID_REQUEST, "File size exceeds server limits"));
                } else {
                    ErrorResponse response = this.createHttpStatusErrorResponse(statusCode);
                    this.sendErrorResponse(context, response);
                }
            } else {
                LOGGER.warn("Global error handler called with null failure and no status code for path: {} user: {}", requestPath, userName);
                this.sendErrorResponse(context, new ErrorResponse(ErrorResponse.ErrorCode.UNKNOWN_ERROR, "Unauthorized"));
            }
        } else {
            Throwable rootCause = this.getRootCause(failure);

            ErrorResponse response;
            if (rootCause instanceof IllegalArgumentException &&
                    rootCause.getMessage() != null &&
                    rootCause.getMessage().contains("Username is null or empty")) {
                response = new ErrorResponse(ErrorResponse.ErrorCode.UNAUTHORIZED, "Authentication required");
            } else {
                response = (ErrorResponse)this.errorMappings.entrySet().stream()
                        .filter((entry) -> (entry.getKey()).isInstance(rootCause))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(new ErrorResponse(ErrorResponse.ErrorCode.UNKNOWN_ERROR, "Unauthorized"));
            }

            if (response.isLogError()) {
                LOGGER.error("Global error handler - Critical error: ", failure);
            } else if (response.getStatus() >= 400 && response.getStatus() < 500) {
                LOGGER.warn("Global error handler - Client error: {} - {}, user: {}, path: {}", new Object[]{response.getCode(), failure.getMessage(), userName, requestPath, failure});
            } else {
                Object[] errBody = new Object[]{response.getCode(), failure.getMessage(), requestPath, null, null};
                String simpleName = rootCause.getClass().getSimpleName();
                errBody[3] = simpleName + ": " + rootCause.getMessage();
                errBody[4] = failure;
                LOGGER.error("Global error handler - Server error: {} - {}, path: {}, rootCause: {}", errBody);
            }

            this.sendErrorResponse(context, response);
        }
    }

    private ErrorResponse createHttpStatusErrorResponse(int statusCode) {
        return switch (statusCode) {
            case 400 -> new ErrorResponse(ErrorResponse.ErrorCode.INVALID_REQUEST, "Bad Request");
            case 401 -> new ErrorResponse(ErrorResponse.ErrorCode.UNAUTHORIZED, "Unauthorized");
            case 403 -> new ErrorResponse(ErrorResponse.ErrorCode.FORBIDDEN, "Forbidden");
            case 404 -> new ErrorResponse(ErrorResponse.ErrorCode.DOCUMENT_NOT_FOUND, "Not Found");
            case 413 -> new ErrorResponse(ErrorResponse.ErrorCode.INVALID_REQUEST, "Payload Too Large");
            case 415 -> new ErrorResponse(ErrorResponse.ErrorCode.INVALID_REQUEST, "Unsupported Media Type");
            case 500 -> new ErrorResponse(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
            case 502 -> new ErrorResponse(ErrorResponse.ErrorCode.CONNECTION_ERROR, "Bad Gateway");
            case 503 -> new ErrorResponse(ErrorResponse.ErrorCode.SERVICE_UNAVAILABLE, "Service Unavailable");
            default -> new ErrorResponse(ErrorResponse.ErrorCode.UNKNOWN_ERROR, "HTTP Error " + statusCode);
        };
    }

    private Throwable getRootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
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