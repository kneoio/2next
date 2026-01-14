package io.kneo.officeframe.controller;

import io.kneo.core.controller.AbstractSecuredController;
import io.kneo.core.dto.actions.ActionBox;
import io.kneo.core.dto.cnst.PayloadType;
import io.kneo.core.dto.form.FormPage;
import io.kneo.core.dto.view.View;
import io.kneo.core.dto.view.ViewPage;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.service.UserService;
import io.kneo.core.util.RuntimeUtil;
import io.kneo.core.util.WebHelper;
import io.kneo.officeframe.dto.LabelDTO;
import io.kneo.officeframe.dto.LabelFilterDTO;
import io.kneo.officeframe.model.Label;
import io.kneo.officeframe.service.LabelService;
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
public class LabelController extends AbstractSecuredController<Label, LabelDTO> {

    @Inject
    LabelService service;

    public LabelController() {
        super(null);
    }

    @Inject
    public LabelController(UserService userService, LabelService service) {
        super(userService);
        this.service = service;
    }

    public void setupRoutes(Router router) {
        String path = "/api/labels";

        BodyHandler jsonBodyHandler = BodyHandler.create().setHandleFileUploads(false);

        router.route(path + "*").handler(this::addHeaders);
        router.route(HttpMethod.GET, path).handler(this::getAll);
        router.route(HttpMethod.GET, path + "/only/category/:category_name").handler(this::getLabelsOfCategory);
        router.route(HttpMethod.GET, path + "/:id").handler(this::get);
        router.route(HttpMethod.POST, path + "/:id?").handler(jsonBodyHandler).handler(this::upsert);
        router.route(HttpMethod.DELETE, path + "/:id").handler(this::delete);
    }

    private void getAll(RoutingContext rc) {
        int page = Integer.parseInt(rc.request().getParam("page", "1"));
        int size = Integer.parseInt(rc.request().getParam("size", "10"));
        LanguageCode languageCode = resolveLanguage(rc);
        String category = rc.request().getParam("category");
        String search = rc.request().getParam("search");

        LabelFilterDTO filter = new LabelFilterDTO();
        if (category != null && !category.isBlank()) {
            filter.setCategory(category);
        }
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
                    View<LabelDTO> dtoEntries = new View<>(tuple.getItem2(),
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

    private void getLabelsOfCategory(RoutingContext rc) {
        String categoryName = rc.pathParam("category_name");
        LanguageCode languageCode = resolveLanguage(rc);

        getContextUser(rc)
                .chain(user -> service.getOfCategory(categoryName, languageCode))
                .subscribe().with(
                        dtoList -> {
                            ViewPage viewPage = new ViewPage();
                            int pageNum = 1;
                            int pageSize = dtoList.size();
                            int count = dtoList.size();
                            View<LabelDTO> dtoEntries = new View<>(dtoList, count, pageNum, 1, pageSize);
                            viewPage.addPayload(PayloadType.VIEW_DATA, dtoEntries);
                            rc.response().setStatusCode(200).end(JsonObject.mapFrom(viewPage).encode());
                        },
                        rc::fail
                );
    }

    private void get(RoutingContext rc) {
        String id = rc.pathParam("id");
        LanguageCode languageCode = resolveLanguage(rc);

        getContextUser(rc)
                .chain(user -> {
                    if ("new".equals(id)) {
                        LabelDTO dto = new LabelDTO();
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

            LabelDTO dto = json.mapTo(LabelDTO.class);
            String id = rc.pathParam("id");
            LanguageCode languageCode = resolveLanguage(rc);

            getContextUser(rc, false, false)
                    .chain(user -> service.upsert(id, dto, user, languageCode))
                    .subscribe().with(
                            label -> rc.response()
                                    .setStatusCode(id == null ? 201 : 200)
                                    .end(JsonObject.mapFrom(label).encode()),
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