package io.kneo.core.dto;

import io.kneo.core.dto.validation.ValidLocalizedName;
import io.kneo.core.localization.LanguageCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.EnumMap;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractReferenceDTO extends AbstractDTO {
    protected String identifier;
    @NotNull(message = "Localized name is required")
    @ValidLocalizedName(
            minLength = 1,
            maxLength = 255,
            allowEmptyMap = false,
            requireDefaultLanguage = true,
            defaultLanguage = LanguageCode.en,
            message = "Invalid localized name format"
    )
    EnumMap<LanguageCode, String> localizedName = new EnumMap<>(LanguageCode.class);
}
