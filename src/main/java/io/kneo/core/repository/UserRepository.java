package io.kneo.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.model.Module;
import io.kneo.core.model.user.*;
import io.kneo.core.server.EnvConst;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlResult;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserRepository extends AsyncRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger("UserRepository");
    private static Map<Long, IUser> userCache = new HashMap<>();
    private static final Map<String, IUser> userAltCache = new HashMap<>();

    public UserRepository() {
        super();
    }

    @Inject
    public UserRepository(PgPool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    CompletionStage<Void> onStart(@Observes StartupEvent ev) {
        return getAll()
                .onItem().transform(users -> users.stream()
                        .filter(u -> u.getId() != null)
                        .map(v -> (IUser) v)
                        .collect(Collectors.toMap(IUser::getId, user -> user)))
                .subscribeAsCompletionStage()
                .thenAccept(cache -> {
                    userCache = cache;
                    userAltCache.putAll(cache.values().stream()
                            .collect(Collectors.toMap(IUser::getUserName, Function.identity())));
                    userAltCache.putAll(cache.values().stream()
                            .collect(Collectors.toMap(IUser::getEmail, Function.identity())));
                });
    }

    public Uni<List<User>> getAll() {
        return client.query(String.format("SELECT * FROM _users LIMIT %d OFFSET 0", 100))
                .execute()
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    public Uni<List<User>> getAll(final int limit, final int offset) {
        String sql = "SELECT * FROM _users";
        if (limit > 0) {
            sql += String.format(" LIMIT %s OFFSET %s", limit, offset);
        }
        return client.query(sql)
                .execute()
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    public Uni<Integer> getAllCount() {
        return client.query("SELECT COUNT(*) FROM _users")
                .execute()
                .onItem().transform(rowSet -> rowSet.iterator().next().getInteger(0));
    }

    public Uni<List<User>> search(String keyword) {
        String query = String.format(
                "SELECT * FROM _users WHERE textsearch @@ to_tsquery('english', '%s')",
                keyword
        );
        return client.query(query)
                .execute()
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    public Multi<IUser> getAllStream() {
        return client.query(String.format("SELECT * FROM _users LIMIT %d OFFSET 0", EnvConst.DEFAULT_PAGE_SIZE))
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().call(row -> Uni.createFrom().item(row).onItem().delayIt().by(Duration.ofMillis(100)))
                .onItem().transform(row -> {
                    User user = new User();
                    user.setLogin(row.getString("login"));
                    return user;
                });
    }

    public Uni<IUser> getId(String login) {
        return client.preparedQuery("SELECT * FROM _users WHERE login = '$1'")
                .execute(Tuple.of(login))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Optional<IUser>> get(Long id) {
        IUser user = userCache.get(id);
        if (user == null) {
            return client.preparedQuery("SELECT * FROM _users WHERE id = $1")
                    .execute(Tuple.of(id))
                    .onItem().transform(RowSet::iterator)
                    .onItem().transform(iterator -> iterator.hasNext() ? Optional.of(from(iterator.next())) : Optional.empty());
        } else {
            return Uni.createFrom().item(user)
                    .onItem().transform(Optional::ofNullable);
        }
    }

    public Uni<Optional<IUser>> findById(long id) {
        if (userCache.isEmpty()) {
            return initializeCache()
                    .onItem().transform(v -> Optional.ofNullable(userCache.get(id)));
        }
        return Uni.createFrom().item(Optional.ofNullable(userCache.get(id)));
    }

    public Uni<IUser> findByLogin(String userName) {
        if (userAltCache.isEmpty()) {
            return initializeCache()
                    .onItem().transform(v -> userAltCache.getOrDefault(userName, UndefinedUser.Build()));
        }
        return Uni.createFrom().item(userAltCache.getOrDefault(userName, UndefinedUser.Build()));
    }

    public Uni<Long> findByIdentifier(String userName) {
        if (userName == null) {
            return Uni.createFrom().item(AnonymousUser.ID);
        }
        if (userAltCache.isEmpty()) {
            return initializeCache()
                    .onItem().transform(v -> userAltCache.get(userName).getId());
        }
        return Uni.createFrom().item(userAltCache.get(userName).getId());
    }

    public Uni<String> getUserName(long id) {
        if (userCache.isEmpty()) {
            return initializeCache()
                    .onItem().transform(v -> userCache.getOrDefault(id, UndefinedUser.Build()).getUserName());
        }
        return Uni.createFrom().item(userCache.getOrDefault(id, UndefinedUser.Build()).getUserName());
    }

    public Uni<Optional<IUser>> getName(Long id) {
        return client.preparedQuery("SELECT * FROM _users WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? Optional.of(from(iterator.next())) : Optional.empty());
    }

    private User from(Row row) {
        User user = new User();
        user.setLogin(row.getString("login"));
        user.setEmail(row.getString("email"));
        user.setDefaultLang(row.getInteger("default_lang"));
        user.setRoles(List.of());
        user.setTimeZone(TimeZone.getDefault());
        user.setId(row.getLong("id"));
        user.setRegDate(row.getLocalDateTime("reg_date").atZone(ZoneId.systemDefault()));
        userCache.put(row.getLong("id"), user);
        return user;
    }

    public Uni<Long> insert(User doc, IUser user) {
        ZonedDateTime nowZonedTime = ZonedDateTime.now();
        LocalDateTime nowLocalDateTime = nowZonedTime.toLocalDateTime();
        String sql = "INSERT INTO _users (author, default_lang, email, i_su, login, reg_date, status, confirmation_code, last_mod_user) VALUES($1, $2, $3, $4, $5, $6, $7, $8, $9) RETURNING id";
        String modulesSQL = "INSERT INTO _user_modules (module_id, user_id, is_on) VALUES($1, $2, $3)";
        String rolesSQL = "INSERT INTO _user_roles (role_id, user_id, is_on) VALUES($1, $2, $3)";
        Tuple params = Tuple.of(user.getId(), doc.getDefaultLang(), doc.getEmail(), doc.isSupervisor(), doc.getLogin(), nowLocalDateTime);
        Tuple finalParams = params.addValue(doc.getRegStatus()).addInteger(doc.getConfirmationCode()).addLong(user.getId());
        return client.withTransaction(tx -> tx.preparedQuery(sql)
                .execute(finalParams)
                .onItem().transform(result -> result.iterator().next().getLong("id"))
                .onItem().transformToUni(id -> {
                    List<Uni<Integer>> userModulesList = new ArrayList<>();
                    for (Module module : doc.getModules()) {
                        userModulesList.add(tx.preparedQuery(modulesSQL)
                                .execute(Tuple.of(module.getId(), id, true))
                                .onItem().transform(SqlResult::rowCount));
                    }
                    if (userModulesList.isEmpty()) {
                        return Uni.createFrom().item(id);
                    } else {
                        return Uni.combine().all().unis(userModulesList).with(results -> id);
                    }
                })
                .onItem().transformToUni(id -> {
                    List<Uni<Integer>> userRolesList = new ArrayList<>();
                    for (Role role : doc.getRoles()) {
                        userRolesList.add(tx.preparedQuery(rolesSQL)
                                .execute(Tuple.of(role.getId(), id, true))
                                .onItem().transform(SqlResult::rowCount));
                    }
                    if (userRolesList.isEmpty()) {
                        return Uni.createFrom().item(id);
                    } else {
                        userCache.clear();
                        userAltCache.clear();
                        return Uni.combine().all().unis(userRolesList).with(results -> id);
                    }
                }).onFailure().recoverWithUni(throwable -> {
                    LOGGER.error(throwable.getMessage(), throwable);
                    return Uni.createFrom().failure(new RuntimeException("Failed to insert user, roles or modules", throwable));
                }));
    }

    public Uni<Long> update(User doc, IUser user) {
        String sql = "UPDATE _users SET default_lang=$1, email=$2, i_su=$3, status=$4, ui_theme=$5, time_zone=$6, last_mod_date=CURRENT_TIMESTAMP, last_mod_user=$7 WHERE id=$8";
        Tuple params = Tuple.of(doc.getDefaultLang(), doc.getEmail(), doc.isSupervisor(), doc.getRegStatus())
                .addValue("cinzento")
                .addValue("0")
                .addLong(user.getId())
                .addLong(doc.getId());

        Uni<Long> longUni = client.preparedQuery(sql)
                .execute(params)
                .onItem().transform(result -> (long) result.rowCount());
        userCache.clear();
        userAltCache.clear();
        return longUni;
    }

    public Uni<Long> delete(Long id) {
        userCache.clear();
        userAltCache.clear();
        return Uni.createFrom().item(1L);
    }

    private Uni<Void> initializeCache() {
        return getAll()
                .onItem().transform(users -> {
                    userCache = users.stream()
                            .filter(u -> u.getId() != null)
                            .collect(Collectors.toMap(IUser::getId, Function.identity()));

                    userAltCache.putAll(users.stream()
                            .collect(Collectors.toMap(IUser::getUserName, Function.identity())));
                    userAltCache.putAll(users.stream()
                            .collect(Collectors.toMap(IUser::getEmail, Function.identity())));
                    return null;
                });
    }
}