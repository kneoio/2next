package com.semantyca.core.controller;

import com.semantyca.core.dto.actions.ActionsFactory;
import com.semantyca.core.dto.cnst.PayloadType;
import com.semantyca.core.dto.view.View;
import com.semantyca.core.dto.view.ViewPage;
import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.core.model.user.AnonymousUser;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.model.user.UndefinedUser;
import com.semantyca.core.repository.exception.UserNotFoundException;
import com.semantyca.core.service.IRESTService;
import com.semantyca.core.service.UserService;
import com.semantyca.core.util.RuntimeUtil;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.semantyca.core.util.RuntimeUtil.countMaxPage;

public abstract class AbstractController<T, V> extends BaseController {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Deprecated
    protected static final String USER_NAME_CLAIM = "preferred_username";
    protected static final String USER_NAME = "username";

    @Getter
    private final UserService userService;

    @Inject
    public AbstractController(UserService userService) {
        this.userService = userService;
    }

    protected void getAll(IRESTService<V> service, RoutingContext rc) {
        int page = Integer.parseInt(rc.request().getParam("page", "0"));
        int size = Integer.parseInt(rc.request().getParam("size", "10"));
        service.getAllCount(AnonymousUser.build())
                .onItem().transformToUni(count -> {
                    int maxPage = countMaxPage(count, size);
                    int pageNum = (page == 0) ? 1 : page;
                    int offset = RuntimeUtil.calcStartEntry(pageNum, size);
                    LanguageCode languageCode = resolveLanguage(rc);
                    return service.getAll(size, offset, languageCode)
                            .onItem().transform(dtoList -> {
                                ViewPage viewPage = new ViewPage();
                                viewPage.addPayload(PayloadType.CONTEXT_ACTIONS, ActionsFactory.getDefaultViewActions(languageCode));
                                View<V> dtoEntries = new View<>(dtoList, count, pageNum, maxPage, size);
                                viewPage.addPayload(PayloadType.VIEW_DATA, dtoEntries);
                                return viewPage;
                            });
                })
                .subscribe().with(
                        viewPage -> rc.response().setStatusCode(200).end(JsonObject.mapFrom(viewPage).encode()),
                        rc::fail
                );

    }

    protected Uni<IUser> getContextUser(RoutingContext rc) {
        return getContextUser(rc, true, false);
    }

    protected Uni<IUser> getContextUser(RoutingContext rc, boolean allowUndefinedUser) {
        return getContextUser(rc, allowUndefinedUser, false);
    }

    protected Uni<IUser> getContextUser(RoutingContext rc, boolean allowUndefinedUser, boolean autoRegisterUser) {
        User vertxUser = rc.user();
        if (vertxUser == null) {
            return Uni.createFrom().failure(new IllegalStateException("No authenticated user found"));
        }
        JsonObject principal = vertxUser.principal();
        String username = principal.getString(USER_NAME);
        if (username == null || username.isEmpty()) {
            username = principal.getString(USER_NAME_CLAIM);
            if (username == null || username.isEmpty()) {
                LOGGER.error("Both username and preferred_username claims are missing from token");
                return Uni.createFrom().failure(new IllegalArgumentException("Username is null or empty"));
            }
            LOGGER.debug("Using preferred_username claim: {}", username);
        }

        String finalUsername = username;
        return userService.findByLogin(username)
                .onItem().transformToUni(user -> {
                    if (user == null || user instanceof UndefinedUser) {
                        if (autoRegisterUser) {
                            return userService.addOrGet(buildUser(finalUsername), List.of(), List.of(), true)
                                    .onItem().transformToUni(userId -> userService.get(userId))
                                    .onItem().transform(Optional::get);
                        } else if (allowUndefinedUser) {
                            return Uni.createFrom().item(buildUser(finalUsername));
                        } else {
                            return Uni.createFrom().failure(new UserNotFoundException(finalUsername));
                        }
                    }
                    return Uni.createFrom().item(user);
                });
    }

    private com.semantyca.core.model.user.User buildUser(String userName) {
        com.semantyca.core.model.user.User newUser = new com.semantyca.core.model.user.User();
        newUser.setLogin(userName);
        if (userName.matches(EMAIL_PATTERN)) {
            newUser.setEmail(userName);
        } else {
            newUser.setEmail(userName + "@fake.local");
        }
        return newUser;
    }


    protected Response postError(Throwable e) {
        Random rand = new Random();
        int randomNum = rand.nextInt(900000) + 100000;
        LOGGER.error("code: {}, msg: {} ", randomNum, e.getMessage(), e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(String.format("code: %s, msg: %s ", randomNum, e.getMessage())).build();
    }

    protected void handleFailure(RoutingContext rc, Throwable throwable) {
        if (throwable instanceof IllegalStateException
                || throwable instanceof IllegalArgumentException
                || throwable instanceof UserNotFoundException) {
            rc.fail(401, throwable);
        } else {
            rc.fail(throwable); // default bubbling
        }
    }

}
