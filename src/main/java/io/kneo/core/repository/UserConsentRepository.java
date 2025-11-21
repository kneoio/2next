package io.kneo.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.model.UserConsent;
import io.kneo.core.model.user.IUser;
import io.kneo.core.repository.exception.DocumentHasNotFoundException;
import io.kneo.core.repository.table.EntityData;
import io.kneo.core.repository.table.TableNameResolver;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static io.kneo.core.repository.table.TableNameResolver.USER_CONSENT_ENTITY_NAME;

@ApplicationScoped
public class UserConsentRepository extends AsyncRepository {
    private static final EntityData entityData = TableNameResolver.create().getEntityNames(USER_CONSENT_ENTITY_NAME);

    @Inject
    public UserConsentRepository(PgPool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    public Uni<List<UserConsent>> getAll(final int limit, final int offset) {
        String sql = String.format("SELECT * FROM %s", entityData.getTableName());
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
        return getAllCount(entityData.getTableName());
    }

    public Uni<UserConsent> findById(UUID uuid) {
        return findById(uuid, entityData, this::from);
    }

    private UserConsent from(Row row) {
        UserConsent doc = new UserConsent();
        setDefaultFields(doc, row);
        doc.setUserId(row.getString("user_id"));
        Boolean essential = row.getBoolean("essential");
        doc.setEssential(essential != null ? essential : true);
        Boolean analytics = row.getBoolean("analytics");
        doc.setAnalytics(analytics != null ? analytics : false);
        Boolean marketing = row.getBoolean("marketing");
        doc.setMarketing(marketing != null ? marketing : false);
        if (row.getLocalDateTime("timestamp") != null) {
            doc.setTimestamp(row.getLocalDateTime("timestamp").atZone(ZoneId.systemDefault()));
        }
        doc.setIpAddress(row.getString("ip_address"));
        doc.setUserAgent(row.getString("user_agent"));
        return doc;
    }

    public Uni<UserConsent> insert(UserConsent doc, IUser user) {
        String sql = String.format("INSERT INTO %s (author, reg_date, last_mod_user, last_mod_date, user_id, essential, analytics, marketing, timestamp, ip_address, user_agent) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) RETURNING id",
                entityData.getTableName());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ts = doc.getTimestamp() != null ? doc.getTimestamp().toLocalDateTime() : now;
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addLong(user.getId())
                .addLocalDateTime(now)
                .addString(doc.getUserId())
                .addBoolean(doc.isEssential())
                .addBoolean(doc.isAnalytics())
                .addBoolean(doc.isMarketing())
                .addLocalDateTime(ts)
                .addString(doc.getIpAddress())
                .addString(doc.getUserAgent());

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transformToUni(result -> {
                    UUID generatedId = result.iterator().next().getUUID("id");
                    return findById(generatedId);
                });
    }

    public Uni<UserConsent> update(UUID id, UserConsent doc, IUser user) {
        String sql = String.format("UPDATE %s SET %s=$1, %s=$2, user_id=$3, essential=$4, analytics=$5, marketing=$6, timestamp=$7, ip_address=$8, user_agent=$9 WHERE id=$10",
                entityData.getTableName(), COLUMN_LAST_MOD_USER, COLUMN_LAST_MOD_DATE);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ts = doc.getTimestamp() != null ? doc.getTimestamp().toLocalDateTime() : now;
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addString(doc.getUserId())
                .addBoolean(doc.isEssential())
                .addBoolean(doc.isAnalytics())
                .addBoolean(doc.isMarketing())
                .addLocalDateTime(ts)
                .addString(doc.getIpAddress())
                .addString(doc.getUserAgent())
                .addUUID(id);

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transformToUni(rowSet -> {
                    if (rowSet.rowCount() == 0) {
                        return Uni.createFrom().failure(new DocumentHasNotFoundException(id));
                    }
                    return findById(id);
                });
    }

    public Uni<Integer> delete(UUID id) {
        String sql = String.format("DELETE FROM %s WHERE id=$1", entityData.getTableName());
        return client.preparedQuery(sql)
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::rowCount);
    }
}
