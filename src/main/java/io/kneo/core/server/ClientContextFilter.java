package io.kneo.core.server;

import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientContextFilter {

    String clientDatabaseUrl = "test";

    @RouteFilter(100)
    void filter(RoutingContext rc) {
        String path = rc.request().uri();
        if (path.startsWith("/api/")) {
            rc.next();
        } else {
            rc.next();
        }
    }
}
