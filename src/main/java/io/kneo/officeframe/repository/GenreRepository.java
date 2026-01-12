package io.kneo.officeframe.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.model.user.IUser;
import io.kneo.core.repository.AsyncRepository;
import io.kneo.core.repository.exception.DocumentHasNotFoundException;
import io.kneo.core.repository.table.EntityData;
import io.kneo.officeframe.model.Genre;
import io.kneo.officeframe.dto.GenreFilterDTO;
import io.kneo.officeframe.repository.table.OfficeFrameNameResolver;
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

import static io.kneo.officeframe.repository.table.OfficeFrameNameResolver.GENRE;

@ApplicationScoped
public class GenreRepository extends AsyncRepository {

    private static final EntityData entityData = OfficeFrameNameResolver.create().getEntityNames(GENRE);
    private static final String BASE_REQUEST = String.format("SELECT * FROM %s", entityData.getTableName());

    @Inject
    public GenreRepository(PgPool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    public Uni<List<Genre>> getAll(final int limit, final int offset) {
        String sql = BASE_REQUEST + " WHERE parent IS NULL";
        if (limit > 0) {
            sql += String.format(" LIMIT %s OFFSET %s", limit, offset);
        }
        return client.query(sql)
                .execute()
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    public Uni<List<Genre>> getAll(final int limit, final int offset, GenreFilterDTO filter) {
        StringBuilder sql = new StringBuilder(BASE_REQUEST);
        Tuple params = null;
        boolean whereAdded = false;
        int index = 0;

        sql.append(" WHERE parent IS NULL");
        whereAdded = true;

        if (filter != null) {
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String likeParam = "%" + filter.getSearch() + "%";
                index++;
                sql.append(whereAdded ? " AND" : " WHERE").append(" identifier ILIKE $").append(index);
                params = params == null ? Tuple.of(likeParam) : params.addString(likeParam);
                whereAdded = true;
            }
        }

        if (limit > 0) {
            sql.append(String.format(" LIMIT %s OFFSET %s", limit, offset));
        }

        if (params == null) {
            return client.query(sql.toString())
                    .execute()
                    .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                    .onItem().transform(this::from)
                    .collect().asList();
        } else {
            return client.preparedQuery(sql.toString())
                    .execute(params)
                    .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                    .onItem().transform(this::from)
                    .collect().asList();
        }
    }

    public Uni<Integer> getAllCount() {
        String sql = String.format("SELECT count(id) FROM %s WHERE parent IS NULL", entityData.getTableName());
        return client.preparedQuery(sql)
                .execute()
                .onItem().transform(rows -> rows.iterator().next().getInteger(0));
    }

    public Uni<Integer> getAllCount(GenreFilterDTO filter) {
        StringBuilder sql = new StringBuilder(String.format("SELECT count(m.id) FROM %s as m", entityData.getTableName()));
        Tuple params = null;
        boolean whereAdded = false;
        int index = 0;

        sql.append(" WHERE m.parent IS NULL");
        whereAdded = true;

        if (filter != null) {
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String likeParam = "%" + filter.getSearch() + "%";
                index++;
                sql.append(whereAdded ? " AND" : " WHERE").append(" m.identifier ILIKE $").append(index);
                params = params == null ? Tuple.of(likeParam) : params.addString(likeParam);
                whereAdded = true;
            }
        }

        if (params == null) {
            return client.preparedQuery(sql.toString())
                    .execute()
                    .onItem().transform(rows -> rows.iterator().next().getInteger(0));
        } else {
            return client.preparedQuery(sql.toString())
                    .execute(params)
                    .onItem().transform(rows -> rows.iterator().next().getInteger(0));
        }
    }

    public Uni<Genre> findById(UUID uuid) {
        return findById(uuid, entityData, this::from);
    }

    public Uni<List<Genre>> getChildrenByParentId(UUID parentId) {
        String sql = BASE_REQUEST + " WHERE parent = $1";
        return client.preparedQuery(sql)
                .execute(Tuple.of(parentId))
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::from)
                .collect().asList();
    }

    private Genre from(Row row) {
        Genre doc = new Genre();
        setDefaultFields(doc, row);
        doc.setIdentifier(row.getString("identifier"));
        doc.setRank(row.getInteger("rank"));
        doc.setColor(row.getString("color"));
        doc.setFontColor(row.getString("font_color"));
        doc.setParent(row.getUUID("parent"));
        setLocalizedNames(doc, row);
        return doc;
    }

    public Uni<Genre> insert(Genre doc, IUser user) {
        String sql = String.format("INSERT INTO %s (id, author, reg_date, last_mod_user, last_mod_date, identifier, rank, loc_name, color, font_color, parent) " +
                        "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) RETURNING id",
                entityData.getTableName());

        JsonObject localizedNameJson = JsonObject.mapFrom(doc.getLocalizedName());
        LocalDateTime now = LocalDateTime.now();
        UUID id = UUID.randomUUID();

        Tuple params = Tuple.of(id)
                .addLong(user.getId())
                .addLocalDateTime(now)
                .addLong(user.getId())
                .addLocalDateTime(now)
                .addString(doc.getIdentifier())
                .addInteger(doc.getRank())
                .addJsonObject(localizedNameJson)
                .addString(doc.getColor())
                .addString(doc.getFontColor())
                .addUUID(doc.getParent());

        return client.preparedQuery(sql)
                .execute(params)
                .onItem().transformToUni(result -> {
                    UUID generatedId = result.iterator().next().getUUID("id");
                    return findById(generatedId);
                });
    }

    public Uni<Genre> update(UUID id, Genre doc, IUser user) {
        String sql = String.format("UPDATE %s SET %s=$1, %s=$2, %s=$3, %s=$4, %s=$5, %s=$6, %s=$7, %s=$8 WHERE id=$9",
                entityData.getTableName(),
                COLUMN_LAST_MOD_USER,
                COLUMN_LAST_MOD_DATE,
                COLUMN_IDENTIFIER,
                COLUMN_RANK,
                COLUMN_LOCALIZED_NAME,
                "color",
                "font_color",
                "parent");

        JsonObject localizedNameJson = JsonObject.mapFrom(doc.getLocalizedName());
        LocalDateTime now = LocalDateTime.now();

        Tuple params = Tuple.of(user.getId())
                .addLocalDateTime(now)
                .addString(doc.getIdentifier())
                .addInteger(doc.getRank())
                .addJsonObject(localizedNameJson)
                .addString(doc.getColor())
                .addString(doc.getFontColor())
                .addUUID(doc.getParent())
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
