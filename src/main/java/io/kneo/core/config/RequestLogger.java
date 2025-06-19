package io.kneo.core.config;

import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RequestLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogger.class);

    void configureRouter(@Observes Router router) {
        // Log ALL incoming requests - highest priority
        router.route().order(-1000).handler(context -> {
            String contentLength = context.request().getHeader("Content-Length");
            String contentType = context.request().getHeader("Content-Type");

         /*   LOGGER.info("=== INCOMING REQUEST === Method: {} {}, Content-Length: {}, Content-Type: {}",
                    context.request().method(),
                    context.request().uri(),
                    contentLength != null ? contentLength : "not-set",
                    contentType != null ? contentType : "not-set");*/

            context.next();
        });

        // Log request completion
        router.route().order(1000).handler(context -> {
            context.addEndHandler(result -> {
          /*      LOGGER.info("=== REQUEST COMPLETED === {} {} -> Status: {}",
                        context.request().method(),
                        context.request().uri(),
                        context.response().getStatusCode());*/
            });
            context.next();
        });
    }
}