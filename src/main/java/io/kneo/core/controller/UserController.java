package io.kneo.core.controller;

import io.kneo.core.dto.cnst.PayloadType;
import io.kneo.core.dto.document.UserDTO;
import io.kneo.core.dto.view.View;
import io.kneo.core.dto.view.ViewPage;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.user.User;
import io.kneo.core.service.UserService;
import io.kneo.core.util.RuntimeUtil;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserController extends AbstractController<User, UserDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private UserService service;

    public UserController() {
        super(null);
    }

    @Inject
    public UserController(UserService service) {
        super(service);
        this.service = service;
    }

    public void setupRoutes(Router router) {
        router.get("/api/users").handler(this::getAll);
        router.post("/api/users").handler(this::create);
        router.put("/api/users/:id").handler(this::update);
        router.delete("/api/users/:id").handler(this::delete);
    }

    private void getAll(RoutingContext rc) {
        int page = Integer.parseInt(rc.request().getParam("page", "1"));
        int size = Integer.parseInt(rc.request().getParam("size", "10"));
        LanguageCode languageCode = resolveLanguage(rc);

        Uni.combine().all().unis(
                        service.getAllCount(),
                        service.getAll(size, (page - 1) * size)
                ).asTuple().map(tuple -> {
                    ViewPage viewPage = new ViewPage();
                    viewPage.addPayload(PayloadType.CONTEXT_ACTIONS, languageCode);
                    View<UserDTO> dtoEntries = new View<>(tuple.getItem2(),
                            tuple.getItem1(), page,
                            RuntimeUtil.countMaxPage(tuple.getItem1(), size),
                            size);
                    viewPage.addPayload(PayloadType.VIEW_DATA, dtoEntries);
                    return viewPage;
                })
                .subscribe().with(
                        viewPage -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(viewPage).encode()),
                        rc::fail
                );
    }

    private void create(RoutingContext rc) {
        try {
            JsonObject jsonObject = rc.getBodyAsJson();
            UserDTO userDTO = jsonObject.mapTo(UserDTO.class);

            service.add(userDTO, false)
                    .subscribe().with(
                            id -> rc.response()
                                    .setStatusCode(201)
                                    .end(),
                            failure -> {
                                LOGGER.error(failure.getMessage(), failure);
                                rc.response()
                                        .setStatusCode(500)
                                        .end(failure.getMessage());
                            }
                    );
        } catch (Exception e) {
            LOGGER.error("Error processing request: {}", e.getMessage());
            rc.response()
                    .setStatusCode(400)
                    .end("Invalid request body");
        }
    }

    private void update(RoutingContext rc) {
        String id = rc.pathParam("id");
        try {
            JsonObject jsonObject = rc.getBodyAsJson();
            UserDTO userDTO = jsonObject.mapTo(UserDTO.class);

            service.upsert(id, userDTO)
                    .subscribe().with(
                            updatedId -> rc.response()
                                    .setStatusCode(200)
                                    .end(),
                            failure -> {
                                LOGGER.error(failure.getMessage(), failure);
                                rc.response()
                                        .setStatusCode(500)
                                        .end(failure.getMessage());
                            }
                    );
        } catch (Exception e) {
            LOGGER.error("Error processing request: {}", e.getMessage());
            rc.response()
                    .setStatusCode(400)
                    .end("Invalid request body");
        }
    }

    private void delete(RoutingContext rc) {
        String id = rc.pathParam("id");
        service.delete(id)
                .subscribe().with(
                        success -> rc.response()
                                .setStatusCode(204)
                                .end(),
                        failure -> {
                            LOGGER.error(failure.getMessage(), failure);
                            rc.response()
                                    .setStatusCode(500)
                                    .end(failure.getMessage());
                        }
                );
    }

}