package io.kneo.core.dto;

import io.kneo.core.dto.validation.ValidLocalizedName;
import com.semantyca.core.model.cnst.LanguageCode;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
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
    @Builder.Default
    EnumMap<LanguageCode, String> localizedName = new EnumMap<>(LanguageCode.class);
}
