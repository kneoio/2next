package kneo.core.server;

import io.kneo.core.controller.*;
import io.kneo.core.server.security.GlobalErrorHandler;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.Router;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Startup
public class ApplicationInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInit.class);

    public final Router router;
    public final String jdbcUrl;
    private final PgPool client;

    private final UserController userController;
    private final LanguageController languageController;
    private final ModuleController moduleController;
    private final RoleController roleController;
    private final WorkspaceController workspaceController;

    /** ✅ FIX: Mark constructor with `@Inject` to allow CDI to initialize dependencies */
    @Inject
    public ApplicationInit(
            PgPool client,
            Router router,
            @ConfigProperty(name = "quarkus.datasource.reactive.url") String jdbcUrl,
            UserController userController,
            LanguageController languageController,
            ModuleController moduleController,
            RoleController roleController,
            WorkspaceController workspaceController
    ) {
        this.client = client;
        this.router = router;
        this.jdbcUrl = jdbcUrl;
        this.userController = userController;
        this.languageController = languageController;
        this.moduleController = moduleController;
        this.roleController = roleController;
        this.workspaceController = workspaceController;
    }

    public void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...{}", EnvConst.APP_ID);
        router.route().failureHandler(new GlobalErrorHandler());
        setupRoutes();

        if (EnvConst.DEV_MODE) {
            LOGGER.info(EnvConst.APP_ID + "'s dev mode enabled");
            LOGGER.info("Database: {}", jdbcUrl);
            checkDatabaseConnection();
        }
    }

    protected void setupRoutes() {
        userController.setupRoutes(router);
        languageController.setupRoutes(router);
        moduleController.setupRoutes(router);
        roleController.setupRoutes(router);
        workspaceController.setupRoutes(router);
    }

    protected void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

    protected void logRegisteredRoutes() {
        LOGGER.info("Registered routes:");
        router.getRoutes().stream()
                .filter(route -> route.getPath() != null && route.methods() != null)
                .filter(route -> !route.getPath().startsWith("/q/"))
                .forEach(route -> LOGGER.info("{} {}", route.methods(), route.getPath()));
    }

    private void checkDatabaseConnection() {
        Uni<String> connected = client.query("SELECT 1")
                .execute()
                .onItem()
                .transform(rows -> "Database connected ...")
                .onFailure()
                .recoverWithItem("Database connection failed");
        LOGGER.info(connected.await().indefinitely());
    }
}
