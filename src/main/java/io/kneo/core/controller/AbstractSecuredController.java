package io.kneo.core.controller;

import io.kneo.core.service.UserService;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.container.ContainerRequestContext;

import java.util.Set;

public abstract class AbstractSecuredController<T, V> extends AbstractController<T, V> {
    protected static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public AbstractSecuredController(UserService userService) {
        super(userService);
    }

    @Deprecated
    protected String getUserOIDCName(ContainerRequestContext requestContext) {
        DefaultJWTCallerPrincipal securityIdentity = (DefaultJWTCallerPrincipal) requestContext.getSecurityContext().getUserPrincipal();
        return securityIdentity.getClaim(USER_NAME_CLAIM);
    }

    protected void addHeaders(RoutingContext rc) {
        rc.response()
                .putHeader("Content-Type", "application/json");
             //   .putHeader("Access-Control-Allow-Origin", "*")
             //   .putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
             //   .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if (rc.request().method() == HttpMethod.OPTIONS) {
            rc.response().setStatusCode(200).end();
        } else {
            rc.next();
        }
    }

    protected void handleValidationErrors(RoutingContext rc, Set<? extends ConstraintViolation<?>> violations) {
        JsonArray errorDetails = new JsonArray();
        for (ConstraintViolation<?> violation : violations) {
            JsonObject errorDetail = new JsonObject()
                    .put("field", violation.getPropertyPath().toString())
                    .put("message", violation.getMessage());
            errorDetails.add(errorDetail);
        }
        JsonObject errorResponse = new JsonObject()
                .put("message", "Validation failed")
                .put("errors", errorDetails);
        rc.response()
                .setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(errorResponse.encode());
    }

    protected void sendJsonError(RoutingContext rc, int statusCode, String errorMessage) {
        JsonObject errorResponse = new JsonObject().put("error", errorMessage);
        rc.response()
                .setStatusCode(statusCode)
                .putHeader("Content-Type", "application/json")
                .end(errorResponse.encode());
    }

    protected void sendJsonError(RoutingContext rc, int statusCode, String errorMessage, String errorDetail) {
        JsonObject errorResponse = new JsonObject()
                .put("error", errorMessage)
                .put("detail", errorDetail);
        rc.response()
                .setStatusCode(statusCode)
                .putHeader("Content-Type", "application/json")
                .end(errorResponse.encode());
    }
}
