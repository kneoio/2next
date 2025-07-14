
package io.kneo.core.controller;

import io.kneo.core.dto.actions.ActionsFactory;
import io.kneo.core.dto.cnst.PayloadType;
import io.kneo.core.dto.document.UserDTO;
import io.kneo.core.dto.form.FormPage;
import io.kneo.core.dto.view.View;
import io.kneo.core.dto.view.ViewPage;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.user.AnonymousUser;
import io.kneo.core.model.user.IUser;
import io.kneo.core.model.user.UndefinedUser;
import io.kneo.core.repository.exception.DocumentModificationAccessException;
import io.kneo.core.repository.exception.UserNotFoundException;
import io.kneo.core.service.AbstractService;
import io.kneo.core.service.IRESTService;
import io.kneo.core.service.UserService;
import io.kneo.core.util.RuntimeUtil;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static io.kneo.core.util.RuntimeUtil.countMaxPage;

public abstract class AbstractController<T, V> extends BaseController {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Deprecated
    protected static final String USER_NAME_CLAIM = "preferred_username";
    protected static final String USER_NAME = "username";

    UserService userService;

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



    @Deprecated
    protected Uni<Response> getAll(IRESTService<V> service, ContainerRequestContext requestContext, int page, int size) throws UserNotFoundException {
        IUser user = getUserId(requestContext);
        String languageHeader = requestContext.getHeaderString("Accept-Language");
        Uni<Integer> countUni = service.getAllCount(AnonymousUser.build());
        Uni<Integer> maxPageUni = countUni.onItem().transform(c -> countMaxPage(c, size));
        Uni<Integer> pageNumUni = Uni.createFrom().item(page);
        Uni<Integer> offsetUni = Uni.combine().all()
                .unis(pageNumUni, Uni.createFrom().item(user.getPageSize()))
                .asTuple()
                .map(tuple -> RuntimeUtil.calcStartEntry(tuple.getItem1(), tuple.getItem2()));
        Uni<List<V>> unis = offsetUni.onItem().transformToUni(offset -> service.getAll(size, offset, LanguageCode.en));
        return Uni.combine().all()
                .unis(unis, offsetUni, pageNumUni, countUni, maxPageUni)
                .asTuple()
                .map(tuple -> {
                    List<V> dtoList = tuple.getItem1();
                    int offset = tuple.getItem2();
                    int pageNum = tuple.getItem3();
                    int count = tuple.getItem4();
                    int maxPage = tuple.getItem5();

                    ViewPage viewPage = new ViewPage();
                    viewPage.addPayload(PayloadType.CONTEXT_ACTIONS, ActionsFactory.getDefaultViewActions(LanguageCode.en));
                    if (pageNum == 0) pageNum = 1;
                    View<V> dtoEntries = new View<>(dtoList, count, pageNum, maxPage, size);
                    viewPage.addPayload(PayloadType.VIEW_DATA, dtoEntries);
                    return Response.ok(viewPage).build();
                });

    }

    @Deprecated
    protected Uni<Response> getById(IRESTService<V> service, String id, ContainerRequestContext requestContext) throws UserNotFoundException {
        IUser user = getUserId(requestContext);
        if (user != null) {
            FormPage page = new FormPage();
            page.addPayload(PayloadType.CONTEXT_ACTIONS, ActionsFactory.getDefaultFormActions(LanguageCode.en));
            return service.getDTO(UUID.fromString(id), user, LanguageCode.en)
                    .onItem().transform(p -> {
                        page.addPayload(PayloadType.DOC_DATA, p);
                        return Response.ok(page).build();
                    })
                    .onFailure().recoverWithItem(t -> {
                        LOGGER.error(t.getMessage(), t);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                    });
        } else {
            throw new UnauthorizedException("User not authorized");
        }
    }

    @Deprecated
    protected IUser getUserId(ContainerRequestContext requestContext) throws UserNotFoundException {
        try {
            DefaultJWTCallerPrincipal securityIdentity = (DefaultJWTCallerPrincipal) requestContext.getSecurityContext().getUserPrincipal();
            return userService.findByLogin(securityIdentity.getClaim(USER_NAME_CLAIM)).await().atMost(TIMEOUT);
        } catch (NullPointerException e) {
            LOGGER.warn("msg: {} ", e.getMessage());
            throw new UserNotFoundException("User not authorized");
        } catch (Exception e) {
            LOGGER.error("msg: {} ", e.getMessage(), e);
            throw new UserNotFoundException("User not authorized");
        }
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
            return Uni.createFrom().failure(new IllegalArgumentException("Username is null or empty"));
        } else {
            return userService.findByLogin(username)
                    .onItem().transformToUni(user -> {
                        if (user == null || user instanceof UndefinedUser) {
                            if (autoRegisterUser) {
                                return userService.add(buildUser(username), List.of(), List.of(), true)
                                        .onItem().transformToUni(userId -> userService.get(userId))
                                        .onItem().transform(Optional::get);
                            } else if (allowUndefinedUser) {
                                return Uni.createFrom().item(buildUser(username));
                            } else {
                                return Uni.createFrom().failure(new UserNotFoundException(username));
                            }
                        }
                        return Uni.createFrom().item(user);
                    });
        }
    }

    private io.kneo.core.model.user.User buildUser(String userName) {
        io.kneo.core.model.user.User newUser = new io.kneo.core.model.user.User();
        newUser.setLogin(userName);
        if (userName.matches(EMAIL_PATTERN)) {
            newUser.setEmail(userName);
        }
        return newUser;
    }

    public Uni<Response> delete(String uuid, AbstractService<T, V> service, @Context ContainerRequestContext requestContext) throws DocumentModificationAccessException, UserNotFoundException {
        IUser userOptional = getUserId(requestContext);
        return service.delete(uuid, userOptional)
                .onItem().transform(count -> Response.ok(count).build());

    }

    protected Response postError(Throwable e) {
        Random rand = new Random();
        int randomNum = rand.nextInt(900000) + 100000;
        LOGGER.error("code: {}, msg: {} ", randomNum, e.getMessage(), e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(String.format("code: %s, msg: %s ", randomNum, e.getMessage())).build();
    }

    protected static String getMimeType(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (fileExtension) {
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "ogg" -> "audio/ogg";
            case "flac" -> "audio/flac";
            default -> "application/octet-stream";
        };
    }

}
