package com.semantyca.mixpla.model;

import com.semantyca.core.model.SecureDataEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Setter
@Getter
public class UserSubscription extends SecureDataEntity<UUID> {
    private long userId;
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private String subscriptionType;
    private String subscriptionStatus;
    private ZonedDateTime trialEnd;
    private ZonedDateTime currentPeriodStart;
    private ZonedDateTime currentPeriodEnd;
    private ZonedDateTime cancelAt;
    private ZonedDateTime canceledAt;
    private boolean active;
    private Integer streamDurationMinutes;
    private boolean otsAllowed;
    private Integer maxSongs;
    private Integer streamQualityKbps;
    private UUID djTypeId;
    private short supportLevel;
    private boolean customScriptAllowed;
}
