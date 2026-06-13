package com.semantyca.mixpla.model;

import com.semantyca.core.model.UserSubscription;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class MixplaUserSubscription extends UserSubscription {
    private ZonedDateTime currentPeriodStart;
    private ZonedDateTime currentPeriodEnd;
    private ZonedDateTime cancelAt;
    private ZonedDateTime canceledAt;
    private Integer streamDurationMinutes;
    private boolean otsAllowed;
    private Integer maxSongs;
    private Integer streamQualityKbps;
    private UUID djTypeId;
    private short supportLevel;
    private boolean customScriptAllowed;
    private Integer maxStations;
    private boolean bulkUploadAllowed;
    private BigDecimal priceEur;
    private List<String> codecs;
}