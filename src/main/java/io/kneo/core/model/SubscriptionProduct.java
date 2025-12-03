package io.kneo.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kneo.core.localization.LanguageCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.EnumMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class SubscriptionProduct extends SimpleReferenceEntity {
    private EnumMap<LanguageCode, String> localizedDescription = new EnumMap<>(LanguageCode.class);
    private String stripePriceId;
    private String stripeProductId;
    private boolean active = true;
}
