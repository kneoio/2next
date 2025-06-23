package io.kneo.core.config;

import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RequestLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogger.class);

    @Inject
    TwoNextConfig config;

    void configureRouter(@Observes Router router) {
        if (!config.getRequestLoggerEnabled()) {
            return;
        }

        router.route().order(-1000).handler(context -> {
            String contentLength = context.request().getHeader("Content-Length");
            String contentType = context.request().getHeader("Content-Type");
            String maskedUri = maskTokenInUri(context.request().uri());

            LOGGER.info("=>: {} {}, Content-Length: {}, Content-Type: {}",
                    context.request().method(),
                    maskedUri,
                    contentLength != null ? contentLength : "not-set",
                    contentType != null ? contentType : "not-set");

            context.next();
        });

        router.route().order(1000).handler(context -> {
            context.addEndHandler(result -> {
                String maskedUri = maskTokenInUri(context.request().uri());
                LOGGER.info("=>{} {} -> Status: {}",
                        context.request().method(),
                        maskedUri,
                        context.response().getStatusCode());
            });
            context.next();
        });
    }

    private String maskTokenInUri(String uri) {
        if (uri == null) {
            return uri;
        }
        return uri.replaceAll("(token=)[^&\\s]*", "$1*****");
    }
}