package io.kneo.core.controller;

import io.kneo.core.dto.cnst.PayloadType;
import io.kneo.core.dto.document.UserDTO;
import io.kneo.core.dto.view.View;
import io.kneo.core.dto.view.ViewPage;
import io.kneo.core.dto.form.FormPage;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.user.User;
import io.kneo.core.service.UserService;
import io.kneo.core.util.RuntimeUtil;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserController extends AbstractSecuredController<User, UserDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private UserService service;

    public UserController() {
        super(null);
    }

    private void getById(RoutingContext rc) {
        String id = rc.pathParam("id");
        LanguageCode languageCode = resolveLanguage(rc);

        getContextUser(rc)
                .chain(user -> {
                    if ("new".equals(id)) {
                        UserDTO dto = new UserDTO();
                        dto.setAuthor(user.getUserName());
                        dto.setLastModifier(user.getUserName());
                        return Uni.createFrom().item(dto);
                    }
                    try {
                        long userId = Long.parseLong(id);
                        return service.getDTO(userId, user, languageCode);
                    } catch (NumberFormatException e) {
                        return Uni.createFrom().failure(new IllegalArgumentException("Invalid user id"));
                    }
                })
                .subscribe().with(
                        dto -> {
                            FormPage page = new FormPage();
                            page.addPayload(PayloadType.DOC_DATA, dto);
                            rc.response().setStatusCode(200).end(JsonObject.mapFrom(page).encode());
                        },
                        rc::fail
                );
    }

    @Inject
    public UserController(UserService service) {
        super(service);
        this.service = service;
    }

    public void setupRoutes(Router router) {
        String path = "/api/users";
        BodyHandler jsonBodyHandler = BodyHandler.create().setHandleFileUploads(false);

        router.route(path + "*").handler(this::addHeaders);
        router.route(HttpMethod.GET, path).handler(this::getAll);
        router.route(HttpMethod.GET, path + "/:id").handler(this::getById);
        router.route(HttpMethod.POST, path + "/:id?").handler(jsonBodyHandler).handler(this::upsert);
        router.route(HttpMethod.DELETE, path + "/:id").handler(this::delete);
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

    private void upsert(RoutingContext rc) {
        try {
            JsonObject json = rc.body().asJsonObject();
            if (json == null) {
                rc.response().setStatusCode(400).end("Request body must be a valid JSON object");
                return;
            }

            UserDTO dto = json.mapTo(UserDTO.class);
            String id = rc.pathParam("id");

            getContextUser(rc, false, false)
                    .chain(u -> service.upsert(id, dto, u))
                    .subscribe().with(
                            ignored -> rc.response()
                                    .setStatusCode(id == null ? 201 : 200)
                                    .end(),
                            throwable -> handleFailure(rc, throwable)
                    );

        } catch (Exception e) {
            LOGGER.error("Error processing request: {}", e.getMessage());
            rc.response().setStatusCode(400).end("Invalid request body");
        }
    }

    private void delete(RoutingContext rc) {
        String id = rc.pathParam("id");
        service.delete(id)
                .subscribe().with(
                        success -> rc.response().setStatusCode(204).end(),
                        failure -> {
                            LOGGER.error(failure.getMessage(), failure);
                            rc.response().setStatusCode(500).end(failure.getMessage());
                        }
                );
    }

}