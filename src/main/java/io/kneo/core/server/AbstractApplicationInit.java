package io.kneo.core.server;

import io.kneo.core.controller.*;
import io.kneo.core.server.security.GlobalErrorHandler;
import io.kneo.officeframe.controller.LabelController;
import io.quarkus.runtime.ShutdownEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.Router;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractApplicationInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApplicationInit.class);

    private final PgPool client;

    @Inject
    UserController userController;
    @Inject
    LanguageController languageController;
    @Inject
    ModuleController moduleController;
    @Inject
    RoleController roleController;
    @Inject
    WorkspaceController workspaceController;
    //OF

    @Inject
    LabelController labelController;



    public AbstractApplicationInit(
            PgPool client
    ) {
        this.client = client;
        LOGGER.info("===== 2next {} =====", EnvConst.VERSION);
    }

    public void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }


    protected void setupRoutes(Router router) {
        router.route().failureHandler(new GlobalErrorHandler());
        userController.setupRoutes(router);
        languageController.setupRoutes(router);
        moduleController.setupRoutes(router);
        roleController.setupRoutes(router);
        workspaceController.setupRoutes(router);
        labelController.setupRoutes(router);
    }

    protected void logRegisteredRoutes(Router router) {
        LOGGER.info("Registered routes 2next:");
        router.getRoutes().stream()
                .filter(route -> route.getPath() != null && route.methods() != null)
                .filter(route -> !route.getPath().startsWith("/q/"))
                .forEach(route -> LOGGER.info("{} {}", route.methods(), route.getPath()));
    }

    public void checkDatabaseConnection() {
        Uni<String> connected = client.query("SELECT 1")
                .execute()
                .onItem()
                .transform(rows -> "Database connected ...")
                .onFailure()
                .recoverWithItem("Database connection failed");
        LOGGER.info(connected.await().indefinitely());
    }
}
