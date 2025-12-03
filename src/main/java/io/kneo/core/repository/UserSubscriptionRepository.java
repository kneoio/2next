package io.kneo.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.model.UserSubscription;
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
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static io.kneo.core.repository.table.TableNameResolver.USER_SUBSCRIPTION_ENTITY_NAME;

@ApplicationScoped
public class UserSubscriptionRepository extends AsyncRepository {
    private static final EntityData entityData = TableNameResolver.create().getEntityNames(USER_SUBSCRIPTION_ENTITY_NAME);

    @Inject
    public UserSubscriptionRepository(PgPool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    public Uni<List<UserSubscription>> getAll(final int limit, final int offset) {
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

    public Uni<UserSubscription> findById(UUID uuid) {
        return findById(uuid, entityData, this::from);
    }

    private UserSubscription from(Row row) {
        UserSubscription doc = new UserSubscription();
        setDefaultFields(doc, row);
        Long uid = row.getLong("user_id");
        if (uid != null) {
            doc.setUserId(uid);
        }
        doc.setStripeSubscriptionId(row.getString("stripe_subscription_id"));
        doc.setSubscriptionType(row.getString("subscription_type"));
        doc.setSubscriptionStatus(row.getString("subscription_status"));
        LocalDateTime te = row.getLocalDateTime("trial_end");
        if (te != null) {
            doc.setTrialEnd(te.atZone(ZoneId.systemDefault()));
        }
        Boolean active = row.getBoolean("active");
        doc.setActive(active != null ? active : false);
        JsonObject meta = row.getJsonObject("meta");
        doc.setMeta(meta);
        return doc;
    }

    public Uni<UserSubscription> insert(UserSubscription doc, IUser user) {
        String sql = String.format("INSERT INTO %s (author, reg_date, last_mod_user, last_mod_date, user_id, stripe_subscription_id, subscription_type, subscription_status, trial_end, active, meta) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) RETURNING id",
                entityData.getTableName());

        LocalDateTime now = LocalDateTime.now();
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addLong(user.getId())
                .addLocalDateTime(now)
                .addLong(doc.getUserId())
                .addString(doc.getStripeSubscriptionId())
                .addString(doc.getSubscriptionType())
                .addString(doc.getSubscriptionStatus())
                .addLocalDateTime(doc.getTrialEnd() != null ? doc.getTrialEnd().toLocalDateTime() : null)
                .addBoolean(doc.isActive())
                .addJsonObject(doc.getMeta() != null ? doc.getMeta() : new JsonObject());

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transformToUni(result -> {
                    UUID generatedId = result.iterator().next().getUUID("id");
                    return findById(generatedId);
                });
    }

    public Uni<UserSubscription> update(UUID id, UserSubscription doc, IUser user) {
        String sql = String.format("UPDATE %s SET %s=$1, %s=$2, user_id=$3, stripe_subscription_id=$4, subscription_type=$5, subscription_status=$6, trial_end=$7, active=$8, meta=$9 WHERE id=$10",
                entityData.getTableName(), COLUMN_LAST_MOD_USER, COLUMN_LAST_MOD_DATE);

        LocalDateTime now = LocalDateTime.now();
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addLong(doc.getUserId())
                .addString(doc.getStripeSubscriptionId())
                .addString(doc.getSubscriptionType())
                .addString(doc.getSubscriptionStatus())
                .addLocalDateTime(doc.getTrialEnd() != null ? doc.getTrialEnd().toLocalDateTime() : null)
                .addBoolean(doc.isActive())
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
