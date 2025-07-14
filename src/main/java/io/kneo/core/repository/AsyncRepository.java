package io.kneo.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.DataEntity;
import io.kneo.core.model.embedded.DocumentAccessInfo;
import io.kneo.core.model.SimpleReferenceEntity;
import io.kneo.core.model.user.IUser;
import io.kneo.core.model.user.User;
import io.kneo.core.repository.exception.DocumentHasNotFoundException;
import io.kneo.core.repository.exception.DocumentModificationAccessException;
import io.kneo.core.repository.rls.RLSRepository;
import io.kneo.core.repository.table.EntityData;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class AsyncRepository extends AbstractRepository{

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());
    protected static final String COLUMN_AUTHOR = "author";
    protected static final String COLUMN_REG_DATE = "reg_date";
    protected static final String COLUMN_LAST_MOD_DATE = "last_mod_date";
    protected static final String COLUMN_LAST_MOD_USER = "last_mod_user";
    protected static final String COLUMN_IDENTIFIER = "identifier";
    protected static final String COLUMN_RANK = "rank";
    protected static final String COLUMN_LOCALIZED_NAME = "loc_name";

    protected PgPool client;
    protected ObjectMapper mapper;
    protected RLSRepository rlsRepository;

    public AsyncRepository() {

    }

    public AsyncRepository(PgPool client, ObjectMapper mapper, RLSRepository rlsRepository) {
        this.client = client;
        this.mapper = mapper;
        this.rlsRepository = rlsRepository;
    }

    protected Uni<Integer> getAllCount(long userID, String mainTable, String aclTable) {
        String sql = String.format("SELECT count(m.id) FROM %s as m, %s as acl WHERE m.id = acl.entity_id AND acl.reader = $1", mainTable, aclTable);
        return client.preparedQuery(sql)
                .execute(Tuple.of(userID))
                .onItem().transform(rows -> rows.iterator().next().getInteger(0));
    }

    public Uni<Integer> getAllCount(String mainTable) {
        String sql = String.format("SELECT count(m.id) FROM %s as m", mainTable);
        return client.preparedQuery(sql)
                .execute()
                .onItem().transform(rows -> rows.iterator().next().getInteger(0));
    }

    public <R> Uni<R> findById(UUID uuid, EntityData entityData, Function<Row, R> fromFunc) {
        return client.preparedQuery("SELECT * FROM " + entityData.getTableName() + " se WHERE se.id = $1")
                .execute(Tuple.of(uuid))
                .onItem().transformToUni(rowSet -> {
                    var iterator = rowSet.iterator();
                    if (iterator.hasNext()) {
                        return Uni.createFrom().item(fromFunc.apply(iterator.next()));
                    } else {
                        return Uni.createFrom().failure(new DocumentHasNotFoundException(uuid));
                    }
                });
    }

    public <R> Uni<R> findByIdentifier(String identifier, EntityData entityData, Function<Row, R> fromFunc) {
        return client.preparedQuery("SELECT * FROM " + entityData.getTableName() + " t WHERE t.identifier = $1")
                .execute(Tuple.of(identifier))
                .onItem().transformToUni(rowSet -> {
                    var iterator = rowSet.iterator();
                    if (iterator.hasNext()) {
                        return Uni.createFrom().item(fromFunc.apply(iterator.next()));
                    } else {
                        return Uni.createFrom().failure(new DocumentHasNotFoundException(entityData.getTableName() + " " + identifier));
                    }
                });
    }

    public Uni<List<DocumentAccessInfo>> getDocumentAccessInfo(UUID documentId, EntityData entityData) {
        String sql = "SELECT rls.reader, rls.reading_time, rls.can_edit, rls.can_delete, u.login, u.i_su " +
                "FROM " + entityData.getRlsName() + " rls " +
                "JOIN _users u ON rls.reader = u.id " +
                "WHERE rls.entity_id = $1 " +
                "ORDER BY u.i_su";

        return client.preparedQuery(sql)
                .execute(Tuple.of(documentId))
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(row -> {
                    DocumentAccessInfo doc = new DocumentAccessInfo();
                    doc.setUserId(row.getLong("reader"));
                    doc.setReadingTime(row.getLocalDateTime("reading_time"));
                    doc.setCanEdit(row.getBoolean("can_edit"));
                    doc.setCanDelete(row.getBoolean("can_delete"));
                    doc.setUserLogin(row.getString("login"));
                    doc.setIsSu(row.getBoolean("i_su"));
                    return doc;
                })
                .collect().asList();
    }

    protected Uni<Integer> delete(UUID uuid, EntityData entityData) {
        String sql = String.format("DELETE FROM %s WHERE id = $1", entityData.getTableName());
        return client.withTransaction(tx -> tx.preparedQuery(sql)
                .execute(Tuple.of(uuid))
                .onItem().transformToUni(rowSet -> {
                    int rowCount = rowSet.rowCount();
                    if (rowCount == 0) {
                        return Uni.createFrom().failure(new DocumentHasNotFoundException(uuid));
                    }
                    return Uni.createFrom().item(rowCount);
                })
                .onFailure().recoverWithUni(throwable -> {
                    LOGGER.error(throwable.getMessage());
                    return Uni.createFrom().failure(new RuntimeException(String.format("Failed to delete %s", entityData.getTableName()), throwable));
                }));
    }

    protected Uni<Void> insertRLSPermissions(io.vertx.mutiny.sqlclient.SqlClient tx, UUID entityId, EntityData entityData, IUser user) {
        String rlsSql = String.format(
                "INSERT INTO %s (reader, entity_id, can_edit, can_delete) VALUES ($1, $2, $3, $4)",
                entityData.getRlsName()
        );

        return tx.preparedQuery(rlsSql)
                .execute(Tuple.of(user.getId(), entityId, true, true))
                .onItem().transformToUni(ignored ->
                        tx.preparedQuery(rlsSql)
                                .execute(Tuple.of(1L, entityId, true, true))
                                .onItem().ignore().andContinueWithNull()
                );
    }

    public Uni<Integer> archive(UUID uuid, EntityData entityData, IUser user) {
        return rlsRepository.findById(entityData.getRlsName(), user.getId(), uuid)
                .onItem().transformToUni(permissions -> {
                    if (!permissions[0]) {
                        return Uni.createFrom().failure(new DocumentModificationAccessException("User does not have edit permission", user.getUserName(), uuid));
                    }

                    String sql = String.format("UPDATE %s SET archived = 1, last_mod_date = $1, last_mod_user = $2 WHERE id = $3",
                            entityData.getTableName());

                    return client.preparedQuery(sql)
                            .execute(Tuple.of(ZonedDateTime.now().toLocalDateTime(), user.getId(), uuid))
                            .onItem().transform(RowSet::rowCount);
                });
    }


    public Uni<Integer> delete(UUID id, EntityData entityData, IUser user) {
        return rlsRepository.findById(entityData.getRlsName(), user.getId(), id)
                .onItem().transformToUni(permissions -> {
                    if (permissions[1]) {
                        String sql = String.format("DELETE FROM %s WHERE id=$1;", entityData.getTableName());
                        return client.withTransaction(tx -> tx.preparedQuery(sql)
                                .execute(Tuple.of(id))
                                .onItem().transformToUni(rowSet -> {
                                    int rowCount = rowSet.rowCount();
                                    if (rowCount == 0) {
                                        return Uni.createFrom().failure(new DocumentHasNotFoundException(id));
                                    }
                                    return Uni.createFrom().item(rowCount);
                                })
                                .onFailure().recoverWithUni(t ->
                                        Uni.createFrom().failure(t)));

                    } else {
                        return Uni.createFrom().failure(new DocumentModificationAccessException("User does not have delete permission", user.getUserName(), id));
                    }
                });
    }

    protected static void setDefaultFields(DataEntity<UUID> entity, Row row) {
        entity.setId(row.getUUID("id"));
        entity.setAuthor(row.getLong(COLUMN_AUTHOR));
        entity.setRegDate(row.getLocalDateTime(COLUMN_REG_DATE).atZone(ZoneId.systemDefault()));
        entity.setLastModifier(row.getLong(COLUMN_LAST_MOD_USER));
        entity.setLastModifiedDate(row.getLocalDateTime(COLUMN_LAST_MOD_DATE).atZone(ZoneId.systemDefault()));
    }


    protected static void setDefaultFields(User entity, Row row) {
        entity.setId(row.getLong("id"));
        entity.setAuthor(row.getLong(COLUMN_AUTHOR));
        entity.setRegDate(row.getLocalDateTime(COLUMN_REG_DATE).atZone(ZoneId.systemDefault()));
        entity.setLastModifier(row.getLong(COLUMN_LAST_MOD_USER));
        entity.setLastModifiedDate(row.getLocalDateTime(COLUMN_LAST_MOD_DATE).atZone(ZoneId.systemDefault()));
    }

    protected static void setLocalizedNames(SimpleReferenceEntity entity, Row row) {
        setLocalizedNames(entity, row, COLUMN_LOCALIZED_NAME);
        JsonObject localizedNameJson = row.getJsonObject(COLUMN_LOCALIZED_NAME);
        if (localizedNameJson != null) {
            EnumMap<LanguageCode, String> localizedName = new EnumMap<>(LanguageCode.class);
            localizedNameJson.getMap().forEach((key, value) -> localizedName.put(LanguageCode.valueOf(key), (String) value));
            entity.setLocalizedName(localizedName);
        }
    }

    protected static void setLocalizedNames(SimpleReferenceEntity entity, Row row, String fieldName) {
        JsonObject localizedNameJson = row.getJsonObject(COLUMN_LOCALIZED_NAME);
        if (localizedNameJson != null) {
            EnumMap<LanguageCode, String> localizedName = new EnumMap<>(LanguageCode.class);
            localizedNameJson.getMap().forEach((key, value) -> localizedName.put(LanguageCode.valueOf(key), (String) value));
            entity.setLocalizedName(localizedName);
        }
    }

    protected static String getBaseSelect(String baseRequest, final int limit, final int offset) {
        String sql = baseRequest;
        if (limit > 0) {
            sql += String.format(" LIMIT %s OFFSET %s", limit, offset);
        }
        return sql;
    }

    protected EnumMap<LanguageCode, String> getLocName(Row row) {
        JsonObject localizedNameJson = row.getJsonObject(COLUMN_LOCALIZED_NAME);
        if (localizedNameJson != null) {
            EnumMap<LanguageCode, String> localizedName = new EnumMap<>(LanguageCode.class);
            localizedNameJson.getMap().forEach((key, value) -> localizedName.put(LanguageCode.valueOf(key), (String) value));
            return localizedName;
        } else {
            return new EnumMap<>(LanguageCode.class);
        }
    }

    protected EnumMap<LanguageCode, String> getLocData(Row row, final String name) {
        JsonObject localizedNameJson = row.getJsonObject(name);
        if (localizedNameJson != null) {
            EnumMap<LanguageCode, String> locData = new EnumMap<>(LanguageCode.class);
            localizedNameJson.getMap().forEach((key, value) -> locData.put(LanguageCode.valueOf(key), (String) value));
            return locData;
        } else {
            return new EnumMap<>(LanguageCode.class);
        }
    }
}