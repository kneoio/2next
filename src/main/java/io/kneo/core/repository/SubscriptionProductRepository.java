package io.kneo.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.model.SubscriptionProduct;
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

import static io.kneo.core.repository.table.TableNameResolver.SUBSCRIPTION_PRODUCT_ENTITY_NAME;

@ApplicationScoped
public class SubscriptionProductRepository extends AsyncRepository {
    private static final EntityData entityData = TableNameResolver.create().getEntityNames(SUBSCRIPTION_PRODUCT_ENTITY_NAME);

    @Inject
    public SubscriptionProductRepository(PgPool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    public Uni<List<SubscriptionProduct>> getAll(final int limit, final int offset) {
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

    public Uni<SubscriptionProduct> findById(UUID uuid) {
        return findById(uuid, entityData, this::from);
    }

    private SubscriptionProduct from(Row row) {
        SubscriptionProduct doc = new SubscriptionProduct();
        setDefaultFields(doc, row);
        doc.setIdentifier(row.getString("identifier"));
        doc.setLocalizedName(getLocName(row));
        doc.setStripePriceId(row.getString("stripe_price_id"));
        doc.setStripeProductId(row.getString("stripe_product_id"));
        doc.setLocalizedDescription(getLocData(row, "loc_descr"));
        Boolean active = row.getBoolean("active");
        doc.setActive(active != null ? active : true);
        return doc;
    }

    public Uni<SubscriptionProduct> insert(SubscriptionProduct doc, IUser user) {
        String sql = String.format("INSERT INTO %s (author, reg_date, last_mod_user, last_mod_date, identifier, stripe_price_id, stripe_product_id, loc_name, loc_descr, active) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10) RETURNING id",
                entityData.getTableName());

        LocalDateTime now = LocalDateTime.now();
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addLong(user.getId())
                .addLocalDateTime(now)
                .addString(doc.getIdentifier())
                .addString(doc.getStripePriceId())
                .addString(doc.getStripeProductId())
                .addJsonObject(JsonObject.mapFrom(doc.getLocalizedName()))
                .addJsonObject(JsonObject.mapFrom(doc.getLocalizedDescription()))
                .addBoolean(doc.isActive());

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transformToUni(result -> {
                    UUID generatedId = result.iterator().next().getUUID("id");
                    return findById(generatedId);
                });
    }

    public Uni<SubscriptionProduct> update(UUID id, SubscriptionProduct doc, IUser user) {
        String sql = String.format("UPDATE %s SET %s=$1, %s=$2, identifier=$3, stripe_price_id=$4, stripe_product_id=$5, loc_name=$6, loc_descr=$7, active=$8 WHERE id=$9",
                entityData.getTableName(), COLUMN_LAST_MOD_USER, COLUMN_LAST_MOD_DATE);

        LocalDateTime now = LocalDateTime.now();
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addString(doc.getIdentifier())
                .addString(doc.getStripePriceId())
                .addString(doc.getStripeProductId())
                .addJsonObject(JsonObject.mapFrom(doc.getLocalizedName()))
                .addJsonObject(JsonObject.mapFrom(doc.getLocalizedDescription()))
                .addBoolean(doc.isActive())
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
