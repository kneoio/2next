package com.semantyca.officeframe.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semantyca.officeframe.dto.LabelFilterDTO;
import com.semantyca.officeframe.model.Label;
import com.semantyca.officeframe.repository.table.OfficeFrameNameResolver;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.repository.AsyncRepository;
import com.semantyca.core.repository.exception.DocumentHasNotFoundException;
import com.semantyca.core.repository.table.EntityData;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.semantyca.officeframe.repository.table.OfficeFrameNameResolver.LABEL;


@ApplicationScoped
public class LabelRepository extends AsyncRepository {

    private static final EntityData entityData = OfficeFrameNameResolver.create().getEntityNames(LABEL);
    private static final String BASE_REQUEST = String.format("SELECT * FROM %s", entityData.getTableName());

    @Inject
    public LabelRepository(Pool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    public Uni<List<Label>> getAll(final int limit, final int offset, Long viewerId, boolean seeAll) {
        String sql = BASE_REQUEST + " WHERE ($1 OR owner IS NULL OR owner = $2)";
        if (limit > 0) {
            sql += String.format(" LIMIT %s OFFSET %s", limit, offset);
        }
        return client.preparedQuery(sql)
                .execute(Tuple.of(seeAll).addLong(viewerId))
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    public Uni<List<Label>> getAll(final int limit, final int offset, LabelFilterDTO filter, Long viewerId, boolean seeAll) {
        StringBuilder sql = new StringBuilder(BASE_REQUEST + " WHERE ($1 OR owner IS NULL OR owner = $2)");
        Tuple params = Tuple.of(seeAll).addLong(viewerId);
        int index = 2;

        if (filter != null) {
            if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
                index++;
                sql.append(" AND category = $").append(index);
                params = params.addString(filter.getCategory());
            }
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String likeParam = "%" + filter.getSearch() + "%";
                index++;
                sql.append(" AND identifier ILIKE $").append(index);
                params = params.addString(likeParam);
            }
        }

        if (limit > 0) {
            sql.append(String.format(" LIMIT %s OFFSET %s", limit, offset));
        }

        return client.preparedQuery(sql.toString())
                .execute(params)
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    public Uni<Integer> getAllCount(Long viewerId, boolean seeAll) {
        String sql = String.format("SELECT count(id) FROM %s WHERE ($1 OR owner IS NULL OR owner = $2)", entityData.getTableName());
        return client.preparedQuery(sql)
                .execute(Tuple.of(seeAll).addLong(viewerId))
                .onItem().transform(rows -> rows.iterator().next().getInteger(0));
    }

    public Uni<Integer> getAllCount(LabelFilterDTO filter, Long viewerId, boolean seeAll) {
        StringBuilder sql = new StringBuilder(String.format("SELECT count(m.id) FROM %s as m WHERE ($1 OR m.owner IS NULL OR m.owner = $2)", entityData.getTableName()));
        Tuple params = Tuple.of(seeAll).addLong(viewerId);
        int index = 2;

        if (filter != null) {
            if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
                index++;
                sql.append(" AND m.category = $").append(index);
                params = params.addString(filter.getCategory());
            }
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String likeParam = "%" + filter.getSearch() + "%";
                index++;
                sql.append(" AND m.identifier ILIKE $").append(index);
                params = params.addString(likeParam);
            }
        }

        return client.preparedQuery(sql.toString())
                .execute(params)
                .onItem().transform(rows -> rows.iterator().next().getInteger(0));
    }

    public Uni<List<Label>> getOfCategory(String categoryName, Long viewerId, boolean seeAll) {
        String sql = String.format("SELECT * FROM %s WHERE category=$1 AND ($2 OR owner IS NULL OR owner = $3)", entityData.getTableName());
        return client.preparedQuery(sql)
                .execute(Tuple.of(categoryName).addBoolean(seeAll).addLong(viewerId))
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    public Uni<Label> findById(UUID uuid) {
        return findById(uuid, entityData, this::from);
    }

    public Uni<List<Label>> findForDocument(UUID uuid, String labelTable) {
        String sql = String.format("SELECT rl.* FROM %s ptl, %s rl where ptl.id = $1 and ptl.label_id = rl.id",
                labelTable, entityData.getTableName());
        return client.preparedQuery(sql)
                .execute(Tuple.of(uuid))
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    public Uni<Label> findByCategoryAndNameOrSlug(String category, String slug, JsonObject localizedName, Long viewerId, boolean seeAll) {
        String sql = BASE_REQUEST + " WHERE category = $1 AND (identifier = $2 OR ($3::jsonb <> '{}'::jsonb AND loc_name @> $3::jsonb)) AND ($4 OR owner IS NULL OR owner = $5) LIMIT 1";
        return client.preparedQuery(sql)
                .execute(Tuple.of(category, slug, localizedName).addBoolean(seeAll).addLong(viewerId))
                .onItem().transform(rows -> {
                    var it = rows.iterator();
                    return it.hasNext() ? from(it.next()) : null;
                });
    }

    public Uni<Label> findByIdentifier(String identifier) {
        return client.preparedQuery(BASE_REQUEST + " WHERE identifier = $1")
                .execute(Tuple.of(identifier))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> {
                    if (iterator.hasNext()) {
                        return from(iterator.next());
                    } else {
                        LOGGER.warn(String.format("No %s found with identifier: " + identifier, entityData.getTableName()));
                        return null;
                    }
                });
    }

    private Label from(Row row) {
        Label doc = new Label();
        setDefaultFields(doc, row);
        doc.setIdentifier(row.getString("identifier"));
        doc.setColor(row.getString("color"));
        doc.setFontColor(row.getString("font_color"));
        doc.setCategory(row.getString("category"));
        doc.setHidden(row.getBoolean("hidden"));
        doc.setParent(row.getUUID("parent"));
        doc.setOwner(row.getLong("owner"));
        setLocalizedNames(doc, row);
        return doc;
    }

    public Uni<Label> insert(Label doc, IUser user) {
        String sql = String.format("INSERT INTO %s (id, author, reg_date, last_mod_user, last_mod_date, identifier, color, font_color, category, parent, hidden, loc_name, owner) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13) RETURNING id",
                entityData.getTableName());

        JsonObject localizedNameJson = JsonObject.mapFrom(doc.getLocalizedName());
        OffsetDateTime now = OffsetDateTime.now();
        UUID id = UUID.randomUUID();

        Tuple params = Tuple.of(id)
                .addLong(user.getId())
                .addOffsetDateTime(now)
                .addLong(user.getId())
                .addOffsetDateTime(now)
                .addString(doc.getIdentifier())
                .addString(doc.getColor())
                .addString(doc.getFontColor())
                .addString(doc.getCategory())
                .addUUID(doc.getParent())
                .addBoolean(doc.isHidden())
                .addJsonObject(localizedNameJson)
                .addValue(doc.getOwner());

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transformToUni(result -> {
                    UUID generatedId = result.iterator().next().getUUID("id");
                    return findById(generatedId);
                });
    }

    public Uni<Label> update(UUID id, Label doc, IUser user) {
        String sql = String.format("UPDATE %s SET %s=$1, %s=$2, %s=$3, %s=$4, %s=$5, %s=$6, %s=$7, %s=$8, %s=$9 WHERE id=$10",
                entityData.getTableName(),
                COLUMN_LAST_MOD_USER,
                COLUMN_LAST_MOD_DATE,
                COLUMN_IDENTIFIER,
                "color",
                "font_color",
                "category",
                "parent",
                "hidden",
                COLUMN_LOCALIZED_NAME);

        JsonObject localizedNameJson = JsonObject.mapFrom(doc.getLocalizedName());
        OffsetDateTime now = OffsetDateTime.now();

        Tuple params = Tuple.of(user.getId())
                .addOffsetDateTime(now)
                .addString(doc.getIdentifier())
                .addString(doc.getColor())
                .addString(doc.getFontColor())
                .addString(doc.getCategory())
                .addUUID(doc.getParent())
                .addBoolean(doc.isHidden())
                .addJsonObject(localizedNameJson)
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
                .onItem().transform(SqlResult::rowCount);
    }
}
