package com.semantyca.core.dto.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.semantyca.core.dto.AbstractDTO;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class UserSubscriptionDTO extends AbstractDTO {
    private long userId;
    private String stripeSubscriptionId;
    private String subscriptionType;
    private String subscriptionStatus;
    private ZonedDateTime trialEnd;
    private boolean active;
    private JsonObject meta;
}
