package com.semantyca.core.controller;

import com.semantyca.core.dto.WorkspacePage;
import com.semantyca.core.dto.document.LanguageDTO;
import com.semantyca.core.model.Language;
import com.semantyca.core.model.user.AnonymousUser;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.service.UserService;
import com.semantyca.core.service.WorkspaceService;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import java.util.Locale;

@ApplicationScoped
public class WorkspaceController extends AbstractSecuredController<Language, LanguageDTO> {

    @Inject
    private WorkspaceService workspaceService;

    public WorkspaceController() {
        super(null);
    }

    public WorkspaceController(UserService userService) {
        super(userService);
    }

    public void setupRoutes(Router router) {
        router.route(HttpMethod.GET, "/core/workspace").handler(requireRoles("admitp")).handler(this::get);
    }

    private void get(RoutingContext rc) {
        String acceptLanguage = rc.request().getHeader(HttpHeaders.ACCEPT_LANGUAGE);
        if (acceptLanguage != null) {
            Locale.forLanguageTag(acceptLanguage.split(",")[0].trim());
        }
        IUser user = AnonymousUser.build();
        workspaceService.getAvailableLanguages()
                .onItem().transform(languages -> {
                    WorkspacePage page = new WorkspacePage(user, languages);
                    return Response.ok(page).build();
                })
                .onFailure().recoverWithItem(this::postError)
                .subscribe().with(
                        response -> rc.response()
                                .setStatusCode(response.getStatus())
                                .end(response.getEntity().toString()),
                        rc::fail
                );
    }
}
