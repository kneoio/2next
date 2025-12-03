package io.kneo.core.dto.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kneo.core.dto.AbstractReferenceDTO;
import io.kneo.core.localization.LanguageCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.EnumMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class SubscriptionProductDTO extends AbstractReferenceDTO {
    private String stripePriceId;
    private String stripeProductId;
    private boolean active;
    private EnumMap<LanguageCode, String> localizedDescription;
}
