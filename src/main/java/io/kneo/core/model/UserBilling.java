package io.kneo.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class UserBilling extends DataEntity<java.util.UUID> {
    private long userId;
    private String stripeCustomerId;
    private JsonObject meta;
}
