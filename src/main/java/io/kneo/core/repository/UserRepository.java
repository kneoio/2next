package io.kneo.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.model.Module;
import io.kneo.core.model.user.*;
import io.kneo.core.repository.cnst.UserRegStatus;
import io.kneo.core.server.EnvConst;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlResult;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@ApplicationScoped
public class UserRepository extends AsyncRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger("UserRepository");

    public UserRepository() {
        super();
    }

    @Inject
    public UserRepository(PgPool client, ObjectMapper mapper) {
        super(client, mapper, null);
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
        return client.preparedQuery("SELECT * FROM _users WHERE login = $1")
                .execute(Tuple.of(login))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Uni<Optional<IUser>> get(Long id) {
        return client.preparedQuery("SELECT * FROM _users WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? Optional.of(from(iterator.next())) : Optional.empty());
    }

    public Uni<Optional<IUser>> findById(long id) {
        return client.preparedQuery("SELECT * FROM _users WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? Optional.of(from(iterator.next())) : Optional.empty());
    }

    public Uni<IUser> findByLogin(String userName) {
        return client.preparedQuery("SELECT * FROM _users WHERE login = $1")
                .execute(Tuple.of(userName))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : UndefinedUser.Build());
    }

    public Uni<IUser> findByTelegramId(String id) {
        return client.preparedQuery("SELECT * FROM _users WHERE telegram_name = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : UndefinedUser.Build());
    }

    public Uni<Long> findByIdentifier(String userName) {
        if (userName == null) {
            return Uni.createFrom().item(AnonymousUser.ID);
        }
        return client.preparedQuery("SELECT id FROM _users WHERE login = $1 OR email = $1")
                .execute(Tuple.of(userName))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? iterator.next().getLong("id") : AnonymousUser.ID);
    }

    public Uni<String> getUserName(long id) {
        return client.preparedQuery("SELECT login FROM _users WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? iterator.next().getString("login") : UndefinedUser.Build().getUserName());
    }

    public Uni<Optional<IUser>> getName(Long id) {
        return client.preparedQuery("SELECT * FROM _users WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? Optional.of(from(iterator.next())) : Optional.empty());
    }

    private User from(Row row) {
        User user = new User();
        setDefaultFields(user, row);
        user.setLogin(row.getString("login"));
        user.setEmail(row.getString("email"));
        user.setDefaultLang(row.getInteger("default_lang"));
        user.setRoles(List.of());
        user.setModules(List.of());

        // Handle time_zone from database or default
        String dbTimeZone = row.getString("time_zone");
        if (dbTimeZone != null && !dbTimeZone.isEmpty()) {
            user.setTimeZone(TimeZone.getTimeZone(dbTimeZone));
        } else {
            user.setTimeZone(TimeZone.getDefault());
        }

        user.setRegStatus(UserRegStatus.getType(row.getInteger("status")));
        user.setSupervisor(row.getBoolean("i_su"));
        return user;
    }

    public Uni<Long> insert(User doc, IUser user) {
        ZonedDateTime nowZonedTime = ZonedDateTime.now();
        LocalDateTime nowLocalDateTime = nowZonedTime.toLocalDateTime();
        String sql = "INSERT INTO _users (author, default_lang, email, i_su, login, reg_date, last_mod_date, status, confirmation_code, last_mod_user, time_zone) VALUES($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) RETURNING id";
        String modulesSQL = "INSERT INTO _modules (module_id, user_id, is_on) VALUES($1, $2, $3)";
        String rolesSQL = "INSERT INTO _roles (role_id, user_id, is_on) VALUES($1, $2, $3)";

        String timeZoneId = doc.getTimeZone() != null ? doc.getTimeZone().getID() : TimeZone.getDefault().getID();

        Tuple finalParams = Tuple.of(user.getId(), doc.getDefaultLang(), doc.getEmail(), doc.isSupervisor(), doc.getLogin(), nowLocalDateTime)
                .addValue(nowLocalDateTime)  // last_mod_date
                .addInteger(doc.getRegStatus().ordinal())  // Convert enum to integer
                .addInteger(doc.getConfirmationCode())
                .addLong(user.getId())
                .addString(timeZoneId);

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
                        return Uni.combine().all().unis(userRolesList).with(results -> id);
                    }
                }).onFailure().recoverWithUni(throwable -> {
                    LOGGER.error(throwable.getMessage(), throwable);
                    return Uni.createFrom().failure(new RuntimeException("Failed to insert user, roles or modules", throwable));
                }));
    }

    public Uni<Long> update(User doc, IUser user) {
        String sql = "UPDATE _users SET default_lang=$1, email=$2, i_su=$3, status=$4, time_zone=$5, last_mod_date=CURRENT_TIMESTAMP, last_mod_user=$6 WHERE id=$7";

        String timeZoneId = doc.getTimeZone() != null ? doc.getTimeZone().getID() : TimeZone.getDefault().getID();

        Tuple params = Tuple.of(doc.getDefaultLang(), doc.getEmail(), doc.isSupervisor())
                .addInteger(doc.getRegStatus().ordinal())  // Convert enum to integer
                .addString(timeZoneId)
                .addLong(user.getId())
                .addLong(doc.getId());

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transform(result -> (long) result.rowCount());
    }

    public Uni<Long> delete(Long id) {
        return client.preparedQuery("DELETE FROM _users WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem().transform(result -> (long) result.rowCount());
    }
}