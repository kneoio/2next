package io.kneo.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.model.UserBilling;
import io.kneo.core.model.user.IUser;
import io.kneo.core.repository.exception.DocumentHasNotFoundException;
import io.kneo.core.repository.table.EntityData;
import io.kneo.core.repository.table.TableNameResolver;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.kneo.core.repository.table.TableNameResolver.USER_BILLING_ENTITY_NAME;

@ApplicationScoped
public class UserBillingRepository extends AsyncRepository {
    private static final EntityData entityData = TableNameResolver.create().getEntityNames(USER_BILLING_ENTITY_NAME);

    @Inject
    public UserBillingRepository(PgPool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    public Uni<List<UserBilling>> getAll(final int limit, final int offset) {
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

    public Uni<UserBilling> findById(UUID uuid) {
        return findById(uuid, entityData, this::from);
    }

    private UserBilling from(Row row) {
        UserBilling doc = new UserBilling();
        setDefaultFields(doc, row);
        Long uid = row.getLong("user_id");
        if (uid != null) {
            doc.setUserId(uid);
        }
        doc.setStripeCustomerId(row.getString("stripe_customer_id"));
        JsonObject meta = row.getJsonObject("meta");
        doc.setMeta(meta);
        return doc;
    }

    public Uni<UserBilling> insert(UserBilling doc, IUser user) {
        String sql = String.format("INSERT INTO %s (author, reg_date, last_mod_user, last_mod_date, user_id, stripe_customer_id, meta) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING id",
                entityData.getTableName());

        LocalDateTime now = LocalDateTime.now();
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addLong(user.getId())
                .addLocalDateTime(now)
                .addLong(doc.getUserId())
                .addString(doc.getStripeCustomerId())
                .addJsonObject(doc.getMeta() != null ? doc.getMeta() : new JsonObject());

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transformToUni(result -> {
                    UUID generatedId = result.iterator().next().getUUID("id");
                    return findById(generatedId);
                });
    }

    public Uni<UserBilling> update(UUID id, UserBilling doc, IUser user) {
        String sql = String.format("UPDATE %s SET %s=$1, %s=$2, user_id=$3, stripe_customer_id=$4, meta=$5 WHERE id=$6",
                entityData.getTableName(), COLUMN_LAST_MOD_USER, COLUMN_LAST_MOD_DATE);

        LocalDateTime now = LocalDateTime.now();
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addLong(doc.getUserId())
                .addString(doc.getStripeCustomerId())
                .addJsonObject(doc.getMeta() != null ? doc.getMeta() : new JsonObject())
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
