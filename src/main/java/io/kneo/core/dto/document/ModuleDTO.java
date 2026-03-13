package io.kneo.core.dto.document;

import com.fasterxml.jackson.annotation.JsonView;
import io.kneo.core.dto.AbstractReferenceDTO;
import io.kneo.core.dto.Views;
import com.semantyca.core.model.cnst.LanguageCode;
import io.kneo.core.server.Environment;
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
public class ModuleDTO extends AbstractReferenceDTO {
    @JsonView(Views.ListView.class)
    @Builder.Default
    private String realm = Environment.realmShortName;
    private boolean isOn;
    @JsonView(Views.ListView.class)
    private EnumMap<LanguageCode, String> localizedName;
    @JsonView(Views.ListView.class)
    private EnumMap<LanguageCode, String> localizedDescription;
}
