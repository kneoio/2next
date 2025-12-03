package io.kneo.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class UserSubscription extends DataEntity<java.util.UUID> {
    private long userId;
    private String stripeSubscriptionId;
    private String subscriptionType;
    private String subscriptionStatus;
    private ZonedDateTime trialEnd;
    private boolean active;
    private JsonObject meta;
}
