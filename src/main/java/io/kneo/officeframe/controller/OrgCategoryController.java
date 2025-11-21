package io.kneo.officeframe.controller;

import io.kneo.core.controller.AbstractSecuredController;
import io.kneo.core.dto.actions.ActionBox;
import io.kneo.core.dto.actions.ActionsFactory;
import io.kneo.core.dto.cnst.PayloadType;
import io.kneo.core.dto.form.FormPage;
import io.kneo.core.dto.view.View;
import io.kneo.core.dto.view.ViewPage;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.service.UserService;
import io.kneo.core.util.RuntimeUtil;
import io.kneo.officeframe.dto.OrgCategoryDTO;
import io.kneo.officeframe.model.OrgCategory;
import io.kneo.officeframe.service.OrgCategoryService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.UUID;

@ApplicationScoped
public class OrgCategoryController extends AbstractSecuredController<OrgCategory, OrgCategoryDTO> {

    private static final Logger LOGGER = Logger.getLogger(OrgCategoryController.class);

    @Inject
    OrgCategoryService service;

    public OrgCategoryController() {
        super(null);
    }

    @Inject
    public OrgCategoryController(UserService userService, OrgCategoryService orgCategoryService) {
        super(userService);
        this.service = orgCategoryService;
    }

    public void setupRoutes(Router router) {
        String path = "/api/orgcategories";

        BodyHandler jsonBodyHandler = BodyHandler.create().setHandleFileUploads(false);

        router.route(path + "*").handler(this::addHeaders);
        router.route(HttpMethod.GET, path).handler(this::get);
        router.route(HttpMethod.GET, path + "/:id").handler(this::getById);
        router.route(HttpMethod.POST, path + "/:id?").handler(jsonBodyHandler).handler(this::upsert);
        router.route(HttpMethod.DELETE, path + "/:id").handler(this::delete);
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
                    viewPage.addPayload(PayloadType.CONTEXT_ACTIONS, ActionsFactory.getDefaultViewActions(languageCode));
                    View<OrgCategoryDTO> dtoEntries = new View<>(tuple.getItem2(),
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
                .chain(user -> {
                    if ("new".equals(id)) {
                        OrgCategoryDTO dto = new OrgCategoryDTO();
                        dto.setAuthor(user.getUserName());
                        dto.setLastModifier(user.getUserName());
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

            OrgCategoryDTO dto = json.mapTo(OrgCategoryDTO.class);
            String id = rc.pathParam("id");

            getContextUser(rc)
                    .chain(user -> service.upsert(id, dto, user, LanguageCode.en))
                    .subscribe().with(
                            doc -> rc.response()
                                    .setStatusCode(id == null ? 201 : 200)
                                    .end(JsonObject.mapFrom(doc).encode()),
                            throwable -> handleFailure(rc, throwable)
                    );

        } catch (Exception e) {
            rc.response().setStatusCode(400).end("Invalid JSON payload");
        }
    }

    private void delete(RoutingContext rc) {
        String id = rc.pathParam("id");

        getContextUser(rc)
                .chain(user -> service.delete(id, user))
                .subscribe().with(
                        count -> rc.response().setStatusCode(count > 0 ? 204 : 404).end(),
                        throwable -> handleFailure(rc, throwable)
                );
    }
}