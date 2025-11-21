package io.kneo.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.model.Agreement;
import io.kneo.core.model.user.IUser;
import io.kneo.core.repository.exception.DocumentHasNotFoundException;
import io.kneo.core.repository.table.EntityData;
import io.kneo.core.repository.table.TableNameResolver;
import io.kneo.officeframe.cnst.CountryCode;
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

import static io.kneo.core.repository.table.TableNameResolver.AGREEMENT_ENTITY_NAME;

@ApplicationScoped
public class AgreementRepository extends AsyncRepository {
    private static final EntityData entityData = TableNameResolver.create().getEntityNames(AGREEMENT_ENTITY_NAME);

    @Inject
    public AgreementRepository(PgPool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    public Uni<List<Agreement>> getAll(final int limit, final int offset) {
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

    public Uni<Agreement> findById(UUID uuid) {
        return findById(uuid, entityData, this::from);
    }

    private Agreement from(Row row) {
        Agreement doc = new Agreement();
        setDefaultFields(doc, row);
        String c = row.getString("country");
        if (c != null) {
            doc.setCountry(CountryCode.valueOf(c));
        }
        doc.setUserAgent(row.getString("user_agent"));
        doc.setAgreementVersion(row.getString("agreement_version"));
        doc.setTermsText(row.getString("terms_text"));
        return doc;
    }

    public Uni<Agreement> insert(Agreement doc, IUser user) {
        String sql = String.format("INSERT INTO %s (author, reg_date, last_mod_user, last_mod_date, country, user_agent, agreement_version, terms_text) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7, $8) RETURNING id",
                entityData.getTableName());

        LocalDateTime now = LocalDateTime.now();
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addLong(user.getId())
                .addLocalDateTime(now)
                .addString(doc.getCountry() != null ? doc.getCountry().name() : null)
                .addString(doc.getUserAgent())
                .addString(doc.getAgreementVersion())
                .addString(doc.getTermsText());

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transformToUni(result -> {
                    UUID generatedId = result.iterator().next().getUUID("id");
                    return findById(generatedId);
                });
    }

    public Uni<Agreement> update(UUID id, Agreement doc, IUser user) {
        String sql = String.format("UPDATE %s SET %s=$1, %s=$2, country=$3, user_agent=$4, agreement_version=$5, terms_text=$6 WHERE id=$7",
                entityData.getTableName(), COLUMN_LAST_MOD_USER, COLUMN_LAST_MOD_DATE);

        LocalDateTime now = LocalDateTime.now();
        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addString(doc.getCountry() != null ? doc.getCountry().name() : null)
                .addString(doc.getUserAgent())
                .addString(doc.getAgreementVersion())
                .addString(doc.getTermsText())
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
