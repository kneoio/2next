package com.semantyca.officeframe.controller;

import com.semantyca.officeframe.dto.GenreDTO;
import com.semantyca.officeframe.dto.GenreFilterDTO;
import com.semantyca.officeframe.model.Genre;
import com.semantyca.officeframe.service.GenreService;
import com.semantyca.core.controller.AbstractSecuredController;
import com.semantyca.core.dto.actions.ActionBox;
import com.semantyca.core.dto.cnst.PayloadType;
import com.semantyca.core.dto.form.FormPage;
import com.semantyca.core.dto.view.View;
import com.semantyca.core.dto.view.ViewPage;
import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.core.service.UserService;
import com.semantyca.core.util.RuntimeUtil;
import com.semantyca.core.util.WebHelper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class GenreController extends AbstractSecuredController<Genre, GenreDTO> {

    @Inject
    GenreService service;

    public GenreController() {
        super(null);
    }

    @Inject
    public GenreController(UserService userService, GenreService service) {
        super(userService);
        this.service = service;
    }

    public void setupRoutes(Router router) {
        String path = "/officeframe/genres";

        BodyHandler jsonBodyHandler = BodyHandler.create().setHandleFileUploads(false);

        router.route(path + "*").handler(this::addHeaders);
        router.route(HttpMethod.GET, path).handler(this::getAll);
        router.route(HttpMethod.GET, path + "/:id").handler(this::get);
        router.route(HttpMethod.POST, path + "/:id?").handler(jsonBodyHandler).handler(this::upsert);
        router.route(HttpMethod.DELETE, path + "/:id").handler(this::delete);
    }

    private void getAll(RoutingContext rc) {
        int page = Integer.parseInt(rc.request().getParam("page", "1"));
        int size = Integer.parseInt(rc.request().getParam("size", "10"));
        LanguageCode languageCode = resolveLanguage(rc);
        String search = rc.request().getParam("search");

        GenreFilterDTO filter = new GenreFilterDTO();
        if (search != null && !search.isBlank()) {
            filter.setSearch(search);
        }

        getContextUser(rc)
                .chain(user -> Uni.combine().all().unis(
                        service.getAllCount(user, filter),
                        service.getAll(size, (page - 1) * size, filter, languageCode)
                ).asTuple().map(tuple -> {
                    ViewPage viewPage = new ViewPage();
                    viewPage.addPayload(PayloadType.CONTEXT_ACTIONS, new ActionBox());
                    View<GenreDTO> dtoEntries = new View<>(tuple.getItem2(),
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

    private void get(RoutingContext rc) {
        String id = rc.pathParam("id");
        LanguageCode languageCode = resolveLanguage(rc);

        getContextUser(rc)
                .chain(user -> {
                    if ("new".equals(id)) {
                        GenreDTO dto = new GenreDTO();
                        dto.setAuthor(user.getUserName());
                        dto.setLastModifier(user.getUserName());
                        dto.setColor(WebHelper.generateRandomBrightColor());
                        return Uni.createFrom().item(dto);
                    }
                    return service.getDTO(UUID.fromString(id), user, languageCode);
                })
                .subscribe().with(
                        dto -> {
                            FormPage page = new FormPage();
                            page.addPayload(PayloadType.CONTEXT_ACTIONS, new ActionBox());
                            page.addPayload(PayloadType.DOC_DATA, dto);
                            rc.response().setStatusCode(200).end(JsonObject.mapFrom(page).encode());
                        },
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

            GenreDTO dto = json.mapTo(GenreDTO.class);
            String id = rc.pathParam("id");
            LanguageCode languageCode = resolveLanguage(rc);

            getContextUser(rc, false, false)
                    .chain(user -> service.upsert(id, dto, user, languageCode))
                    .subscribe().with(
                            genre -> rc.response()
                                    .setStatusCode(id == null ? 201 : 200)
                                    .end(JsonObject.mapFrom(genre).encode()),
                            throwable -> handleFailure(rc, throwable)
                    );

        } catch (Exception e) {
            rc.response().setStatusCode(400).end("Invalid JSON payload");
        }
    }

    private void delete(RoutingContext rc) {
        String id = rc.pathParam("id");

        getContextUser(rc, false, false)
                .chain(user -> service.delete(id, user))
                .subscribe().with(
                        count -> rc.response().setStatusCode(count > 0 ? 204 : 404).end(),
                        throwable -> handleFailure(rc, throwable)
                );
    }
}
