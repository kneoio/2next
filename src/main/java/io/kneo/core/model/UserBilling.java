package io.kneo.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class UserBilling extends DataEntity<UUID> {
    private long userId;
    private String stripeCustomerId;
    private JsonObject meta;
}
