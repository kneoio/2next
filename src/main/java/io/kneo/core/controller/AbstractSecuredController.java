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

    public AbstractSecuredController(UserService userService) {
        super(userService);
    }

    @Deprecated
    protected String getUserOIDCName(ContainerRequestContext requestContext) {
        DefaultJWTCallerPrincipal securityIdentity = (DefaultJWTCallerPrincipal) requestContext.getSecurityContext().getUserPrincipal();
        return securityIdentity.getClaim(USER_NAME_CLAIM);
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

    /*protected void handleUpsertFailure(RoutingContext rc, Throwable throwable) {
        if (throwable instanceof io.kneo.core.repository.exception.DocumentModificationAccessException) {
            rc.response().setStatusCode(403).end("Not enough rights to update");
        } else if (throwable instanceof io.kneo.broadcaster.repository.exceptions.UploadAbsenceException) {
            rc.response().setStatusCode(400).end(throwable.getMessage());
        } else {
            rc.fail(throwable);
        }
    }*/

    protected void sendUpsertResponse(RoutingContext rc, Object doc, String id) {
        rc.response()
                .setStatusCode(id == null ? 201 : 200)
                .end(JsonObject.mapFrom(doc).encode());
    }

}