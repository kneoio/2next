package com.semantyca.mixpla.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semantyca.core.repository.AsyncRepository;
import com.semantyca.mixpla.model.MixplaUserSubscription;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class UserSubscriptionRepository extends AsyncRepository {

    private static final String TABLE = "mixpla__user_subscriptions";

    protected UserSubscriptionRepository() {
        super(null, null, null);
    }

    @Inject
    public UserSubscriptionRepository(Pool client, ObjectMapper mapper) {
        super(client, mapper, null);
    }

    public Uni<List<MixplaUserSubscription>> findByUserId(Long userId) {
        String sql = String.format("SELECT * FROM %s WHERE user_id=$1 ORDER BY reg_date DESC", TABLE);
        return client.preparedQuery(sql)
                .execute(Tuple.of(userId))
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(this::fromRow)
                .collect().asList();
    }

    public Uni<MixplaUserSubscription> findActiveByUserId(Long userId) {
        String sql = String.format("SELECT * FROM %s WHERE user_id=$1 AND active=true", TABLE);
        return client.preparedQuery(sql)
                .execute(Tuple.of(userId))
                .onItem().transform(rows -> {
                    var it = rows.iterator();
                    return it.hasNext() ? fromRow(it.next()) : null;
                });
    }

    private MixplaUserSubscription fromRow(Row row) {
        MixplaUserSubscription userSubscription = new MixplaUserSubscription();
        userSubscription.setId(row.getUUID("id"));
        userSubscription.setUserId(row.getLong("user_id"));
        userSubscription.setStripeCustomerId(row.getString("stripe_customer_id"));
        userSubscription.setStripeSubscriptionId(row.getString("stripe_subscription_id"));
        userSubscription.setSubscriptionType(row.getString("subscription_type"));
        userSubscription.setSubscriptionStatus(row.getString("subscription_status"));
        var trialEnd = row.getOffsetDateTime("trial_end");
        if (trialEnd != null) userSubscription.setTrialEnd(trialEnd.toZonedDateTime());
        var periodStart = row.getOffsetDateTime("current_period_start");
        if (periodStart != null) userSubscription.setCurrentPeriodStart(periodStart.toZonedDateTime());
        var periodEnd = row.getOffsetDateTime("current_period_end");
        if (periodEnd != null) userSubscription.setCurrentPeriodEnd(periodEnd.toZonedDateTime());
        var cancelAt = row.getOffsetDateTime("cancel_at");
        if (cancelAt != null) userSubscription.setCancelAt(cancelAt.toZonedDateTime());
        var canceledAt = row.getOffsetDateTime("canceled_at");
        if (canceledAt != null) userSubscription.setCanceledAt(canceledAt.toZonedDateTime());
        userSubscription.setActive(Boolean.TRUE.equals(row.getBoolean("active")));
        userSubscription.setStreamDurationMinutes(row.getInteger("stream_duration_minutes"));
        userSubscription.setOtsAllowed(Boolean.TRUE.equals(row.getBoolean("ots_allowed")));
        userSubscription.setMaxSongs(row.getInteger("max_songs"));
        userSubscription.setStreamQualityKbps(row.getInteger("stream_quality_kbps"));
        Object djTypeVal = row.getValue("dj_type");
        userSubscription.setDjType(codecsFromJson(djTypeVal != null ? djTypeVal.toString() : "[]"));
        userSubscription.setSupportLevel(row.getShort("support_level"));
        userSubscription.setCustomScriptAllowed(Boolean.TRUE.equals(row.getBoolean("custom_script_allowed")));
        userSubscription.setMaxStations(row.getInteger("max_stations"));
        userSubscription.setPriceEur(row.getBigDecimal("price_eur"));
        Object codecsVal = row.getValue("codecs");
        userSubscription.setCodecs(codecsFromJson(codecsVal != null ? codecsVal.toString() : "[]"));
        return userSubscription;
    }

    private List<String> codecsFromJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

}
