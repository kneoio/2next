package io.kneo.core.controller;

import io.kneo.core.dto.cnst.PayloadType;
import io.kneo.core.dto.document.ModuleDTO;
import io.kneo.core.dto.view.View;
import io.kneo.core.dto.view.ViewPage;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.Module;
import io.kneo.core.model.user.IUser;
import io.kneo.core.service.ModuleService;
import io.kneo.core.service.UserService;
import io.kneo.core.util.RuntimeUtil;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class ModuleController extends AbstractSecuredController<Module, ModuleDTO> {

    @Inject
    ModuleService service;

    public ModuleController() {
        super(null);
    }

    @Inject
    public ModuleController(UserService userService, ModuleService moduleService) {
        super(userService);
        this.service = moduleService;
    }

    public void setupRoutes(Router router) {
        router.route(HttpMethod.GET, "/api/modules").handler(this::get);
        router.route(HttpMethod.GET, "/api/modules/:id").handler(this::getById);
        router.route(HttpMethod.PUT, "/api/modules/:id").handler(this::upsert);
        router.route(HttpMethod.DELETE, "/api/modules/:id").handler(this::delete);
    }

    private void get(RoutingContext rc) {
        int page = Integer.parseInt(rc.request().getParam("page", "1"));
        int size = Integer.parseInt(rc.request().getParam("size", "10"));
        LanguageCode languageCode = resolveLanguage(rc);

        getContextUser(rc)
                .chain(user -> Uni.combine().all().unis(
                        service.getAllCount(user),
                        service.getAll(size, (page - 1) * size, languageCode)
                ).asTuple().map(tuple -> {
                    ViewPage viewPage = new ViewPage();
                    View<ModuleDTO> dtoEntries = new View<>(tuple.getItem2(),
                            tuple.getItem1(), page,
                            RuntimeUtil.countMaxPage(tuple.getItem1(), size),
                            size);
                    viewPage.addPayload(PayloadType.VIEW_DATA, dtoEntries);
                    return viewPage;
                }))
                .subscribe().with(
                        viewPage -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(viewPage).encode()),
                        rc::fail
                );
    }

    private void getById(RoutingContext rc) {
        String id = rc.pathParam("id");
        LanguageCode languageCode = resolveLanguage(rc);

        getContextUser(rc)
                .chain(user -> service.getDTO(UUID.fromString(id), user, languageCode))
                .subscribe().with(
                        module -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(module).encode()),
                        rc::fail
                );
    }

    private void upsert(RoutingContext rc) {
        String id = rc.pathParam("id");
        JsonObject json = rc.body().asJsonObject();
        ModuleDTO dto = json.mapTo(ModuleDTO.class);
        LanguageCode languageCode = resolveLanguage(rc);

        getContextUser(rc)
                .chain(user -> service.upsert(id, dto, user, languageCode))
                .subscribe().with(
                        updated -> rc.response().setStatusCode(200).end(),
                        throwable -> handleFailure(rc, throwable)
                );
    }

    private void delete(RoutingContext rc) {
        String id = rc.pathParam("id");

        getContextUser(rc)
                .chain(user -> service.delete(id, user))
                .subscribe().with(
                        count -> {
                            if (count > 0) {
                                rc.response().setStatusCode(200).end();
                            } else {
                                rc.fail(404);
                            }
                        },
                        throwable -> handleFailure(rc, throwable)
                );
    }
}