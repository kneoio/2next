package io.kneo.core.server;

import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.web.Router;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CoreApplicationInit extends AbstractApplicationInit{
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreApplicationInit.class);

    @Inject
    Router router;

    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    String jdbcUrl;


    @Inject
    public CoreApplicationInit(PgPool client) {
        super(client);
    }

    public CoreApplicationInit() {
        super(null);
    }

    public void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...{}", EnvConst.APP_ID);
        super.setupRoutes(router);
        logRegisteredRoutes(router);

        if (EnvConst.DEV_MODE) {
            LOGGER.info(EnvConst.APP_ID + "'s dev mode enabled");
        }
    }

}
