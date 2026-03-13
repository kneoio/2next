package com.semantyca.core.dto.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.semantyca.core.dto.AbstractReferenceDTO;
import com.semantyca.core.dto.Views;
import com.semantyca.core.model.cnst.LanguageCode;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModuleDTO extends AbstractReferenceDTO {
    @JsonView(Views.ListView.class)
    private String realm;
    private boolean isOn;
    private boolean isPublic;
    @JsonView(Views.ListView.class)
    private EnumMap<LanguageCode, String> localizedName;
    @JsonView(Views.ListView.class)
    private EnumMap<LanguageCode, String> localizedDescription;
    @JsonView(Views.ListView.class)
    private int position;
    private String theme;
}
