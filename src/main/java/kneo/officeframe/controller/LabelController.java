package kneo.officeframe.controller;

import io.kneo.core.controller.AbstractSecuredController;
import io.kneo.core.dto.actions.ActionBox;
import io.kneo.core.dto.cnst.PayloadType;
import io.kneo.core.dto.form.FormPage;
import io.kneo.core.dto.view.View;
import io.kneo.core.dto.view.ViewPage;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.service.UserService;
import io.kneo.core.util.RuntimeUtil;
import io.kneo.officeframe.dto.LabelDTO;
import io.kneo.officeframe.model.Label;
import io.kneo.officeframe.service.LabelService;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

import static io.kneo.core.util.RuntimeUtil.countMaxPage;

@ApplicationScoped
public class LabelController extends AbstractSecuredController<Label, LabelDTO> {

    @Inject
    LabelService service;

    public LabelController() {
        super(null);
    }

    public LabelController(UserService userService, LabelService service) {
        super(userService);
        this.service = service;
    }

    public void setupRoutes(Router router) {
        router.route(HttpMethod.GET, "/api/:org/labels").handler(this::getAll);
        router.route(HttpMethod.GET, "/api/:org/labels/only/category/:category_name").handler(this::getLabelsOfCategory);
        router.route(HttpMethod.GET, "/api/:org/labels/:id").handler(this::get);
        router.route(HttpMethod.GET, "/api/:org/labels/identifier/:id").handler(this::getByIdentifier);
        router.route(HttpMethod.POST, "/api/:org/labels/:id").handler(this::upsert);
        router.route(HttpMethod.DELETE, "/api/:org/labels/:id").handler(this::delete);
    }

    private void getAll(RoutingContext rc) {
        int page = Integer.parseInt(rc.request().getParam("page", "0"));
        int size = Integer.parseInt(rc.request().getParam("size", "10"));
        service.getAllCount()
                .onItem().transformToUni(count -> {
                    int maxPage = countMaxPage(count, size);
                    int pageNum = (page == 0) ? 1 : page;
                    int offset = RuntimeUtil.calcStartEntry(pageNum, size);
                    LanguageCode languageCode = resolveLanguage(rc);
                    return service.getAll(size, offset, languageCode)
                            .onItem().transform(dtoList -> {
                                ViewPage viewPage = new ViewPage();
                                viewPage.addPayload(PayloadType.CONTEXT_ACTIONS, new ActionBox());
                                View<LabelDTO> dtoEntries = new View<>(dtoList, count, pageNum, maxPage, size);
                                viewPage.addPayload(PayloadType.VIEW_DATA, dtoEntries);
                                return viewPage;
                            });
                })
                .subscribe().with(
                        viewPage -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(viewPage).encode()),
                        rc::fail
                );
    }

    private void getLabelsOfCategory(RoutingContext rc) {
        LanguageCode languageCode = resolveLanguage(rc);
        service.getOfCategory(rc.pathParam("category_name"), languageCode)
                .onItem().transform(dtoList -> {
                    ViewPage viewPage = new ViewPage();
                    int pageNum = 1;
                    int pageSize = dtoList.size();
                    int count = dtoList.size();
                    View<LabelDTO> dtoEntries = new View<>(dtoList, count, pageNum, 1, pageSize);
                    viewPage.addPayload(PayloadType.VIEW_DATA, dtoEntries);
                    return viewPage;
                })
                .subscribe().with(
                        viewPage -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(viewPage).encode()),
                        rc::fail
                );
    }

    private void get(RoutingContext rc)  {
        FormPage page = new FormPage();
        page.addPayload(PayloadType.CONTEXT_ACTIONS, new ActionBox());
        service.getDTO(UUID.fromString(rc.pathParam("id")), getUser(rc), resolveLanguage(rc))
                .onItem().transform(dto -> {
                    page.addPayload(PayloadType.DOC_DATA, dto);
                    return page;
                })
                .subscribe().with(
                        formPage -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(formPage).encode()),
                        rc::fail
                );
    }

    private void getByIdentifier(RoutingContext rc)  {
        FormPage page = new FormPage();
        page.addPayload(PayloadType.CONTEXT_ACTIONS, new ActionBox());
        service.getDTOByIdentifier(rc.pathParam("id"))
                .onItem().transform(dto -> {
                    page.addPayload(PayloadType.DOC_DATA, dto);
                    return page;
                })
                .subscribe().with(
                        formPage -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(formPage).encode()),
                        rc::fail
                );
    }

    private void upsert(RoutingContext rc) {
        JsonObject jsonObject = rc.body().asJsonObject();
        LabelDTO dto = jsonObject.mapTo(LabelDTO.class);
        String id = rc.pathParam("id");
        service.upsert(id, dto, getUser(rc), resolveLanguage(rc))
                .subscribe().with(
                        label -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(label).encode()),
                        rc::fail
                );
    }

    private void delete(RoutingContext rc) {
        rc.response().setStatusCode(200).end();
    }
}