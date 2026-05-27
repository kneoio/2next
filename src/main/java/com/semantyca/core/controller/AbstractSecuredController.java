package com.semantyca.core.controller;

import com.semantyca.core.repository.exception.DocumentModificationAccessException;
import com.semantyca.core.repository.exception.UploadAbsenceException;
import com.semantyca.core.service.UserService;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import jakarta.validation.ConstraintViolation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AbstractSecuredController<T, V> extends AbstractController<T, V> {

    public AbstractSecuredController(UserService userService) {
        super(userService);
    }

    protected void addHeaders(RoutingContext rc) {
        if (rc.request().method() == HttpMethod.OPTIONS) {
            rc.response().setStatusCode(200).end();
        } else if (rc.request().method() != HttpMethod.DELETE) {
            rc.response().putHeader("Content-Type", "application/json");
            rc.next();
        } else {
            rc.next();
        }
    }

    protected boolean validateJsonBody(RoutingContext rc) {
        JsonObject json = rc.body().asJsonObject();
        if (json == null) {
            rc.response().setStatusCode(400).end("Request body must be a valid JSON object");
            return false;
        }
        return true;
    }

    protected <D> boolean validateDTO(RoutingContext rc, D dto, jakarta.validation.Validator validator) {
        Set<ConstraintViolation<D>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            handleValidationErrors(rc, violations);
            return false;
        }
        return true;
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

    protected void handleUpsertFailure(RoutingContext rc, Throwable throwable) {
        if (throwable instanceof DocumentModificationAccessException) {
            rc.response().setStatusCode(403).end("Not enough rights to update");
        } else if (throwable instanceof UploadAbsenceException) {
            rc.response().setStatusCode(400).end(throwable.getMessage());
        } else {
            rc.fail(throwable);
        }
    }

    protected void sendUpsertResponse(RoutingContext rc, Object doc, String id) {
        rc.response()
                .setStatusCode(id == null ? 201 : 200)
                .end(JsonObject.mapFrom(doc).encode());
    }

    /**
     * Returns a route handler that enforces role membership before passing to the next handler.
     * The user must have AT LEAST ONE of the given roles (OR logic).
     * Roles are read from the Keycloak JWT claim: realm_access.roles
     *
     * Usage in setupRoutes():
     *   router.route(HttpMethod.DELETE, path + "/:id")
     *       .handler(requireRoles("admin"))
     *       .handler(this::delete);
     */
    protected Handler<RoutingContext> requireRoles(String... roles) {
        List<String> required = Arrays.asList(roles);
        return rc -> {
            if (hasAnyRole(rc, required)) {
                rc.next();
            } else {
                rc.response()
                        .setStatusCode(403)
                        .putHeader("Content-Type", "application/json")
                        .end(new JsonObject()
                                .put("message", "Forbidden: insufficient role")
                                .put("required", required.toString())  //should be hidden
                                .encode());
            }
        };
    }

    /**
     * Inline check — returns true if the current user has the given role.
     * Roles are read from the Keycloak JWT claim: realm_access.roles
     */
    protected boolean hasRole(RoutingContext rc, String role) {
        return hasAnyRole(rc, Collections.singletonList(role));
    }

    private boolean hasAnyRole(RoutingContext rc, List<String> roles) {
        String authHeader = rc.request().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        try {
            String[] parts = authHeader.substring(7).split("\\.");
            if (parts.length < 2) return false;
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonObject claims = new JsonObject(payload);
            JsonObject realmAccess = claims.getJsonObject("realm_access");
            if (realmAccess == null) return false;
            JsonArray realmRoles = realmAccess.getJsonArray("roles");
            if (realmRoles == null) return false;
            for (String role : roles) {
                if (realmRoles.contains(role)) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}