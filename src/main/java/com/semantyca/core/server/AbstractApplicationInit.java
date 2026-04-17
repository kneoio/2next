package com.semantyca.core.server;

import com.semantyca.core.controller.LanguageController;
import com.semantyca.core.controller.ModuleController;
import com.semantyca.core.controller.RoleController;
import com.semantyca.core.controller.SubscriptionProductController;
import com.semantyca.core.controller.UserBillingController;
import com.semantyca.core.controller.UserController;
import com.semantyca.core.controller.UserSubscriptionController;
import com.semantyca.core.controller.WorkspaceController;
import com.semantyca.core.server.security.GlobalErrorHandler;
import com.semantyca.core.controller.AgreementController;
import com.semantyca.core.controller.UserConsentController;
import com.semantyca.officeframe.controller.GenreController;
import com.semantyca.officeframe.controller.LabelController;
import io.quarkus.runtime.ShutdownEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.Router;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AbstractApplicationInit {

    private static final Logger LOGGER = Logger.getLogger(AbstractApplicationInit.class);

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

    @Inject
    GenreController genreController;

    @Inject
    AgreementController agreementController;

    @Inject
    UserConsentController userConsentController;

    @Inject
    UserBillingController userBillingController;

    @Inject
    UserSubscriptionController userSubscriptionController;

    @Inject
    SubscriptionProductController subscriptionProductController;

    @Inject
    EndpointConfig endpointConfig;

    private Set<String> enabledControllers;


    public AbstractApplicationInit(
            PgPool client
    ) {
        this.client = client;
        LOGGER.infof("===== 2next %s =====", EnvConst.VERSION);
    }

    public void onStop(@Observes ShutdownEvent ev) {
        LOGGER.infof("The application %s is stopping...", EnvConst.APP_ID);
    }


    protected void setupRoutes(Router router) {
        parseEnabledControllers();
        
        router.route().failureHandler(new GlobalErrorHandler());
        
        if (isControllerEnabled("users")) {
            userController.setupRoutes(router);
        }
        if (isControllerEnabled("languages")) {
            languageController.setupRoutes(router);
        }
        if (isControllerEnabled("modules")) {
            moduleController.setupRoutes(router);
        }
        if (isControllerEnabled("roles")) {
            roleController.setupRoutes(router);
        }
        if (isControllerEnabled("workspaces")) {
            workspaceController.setupRoutes(router);
        }
        if (isControllerEnabled("labels")) {
            labelController.setupRoutes(router);
        }
        if (isControllerEnabled("genres")) {
            genreController.setupRoutes(router);
        }
        if (isControllerEnabled("agreements")) {
            agreementController.setupRoutes(router);
        }
        if (isControllerEnabled("user-consents")) {
            userConsentController.setupRoutes(router);
        }
        if (isControllerEnabled("user-billing")) {
            userBillingController.setupRoutes(router);
        }
        if (isControllerEnabled("user-subscriptions")) {
            userSubscriptionController.setupRoutes(router);
        }
        if (isControllerEnabled("subscription-products")) {
            subscriptionProductController.setupRoutes(router);
        }
    }

    protected void logRegisteredRoutes(Router router) {
        LOGGER.info("Registered routes 2next:");
        router.getRoutes().stream()
                .filter(route -> route.getPath() != null && route.methods() != null)
                .filter(route -> !route.getPath().startsWith("/q/"))
                .forEach(route -> LOGGER.infof("%s %s", route.methods(), route.getPath()));
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

    private void parseEnabledControllers() {
        String enabled = endpointConfig.enabled();
        enabledControllers = Arrays.stream(enabled.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private boolean isControllerEnabled(String controllerName) {
        return enabledControllers.contains(controllerName);
    }
}
