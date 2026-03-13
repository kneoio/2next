package com.semantyca.core.dto.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.semantyca.core.dto.AbstractDTO;
import com.semantyca.core.dto.Views;
import com.semantyca.core.model.cnst.LanguageCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.EnumMap;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "position", "localizedName"})
public class LanguageDTO extends AbstractDTO {
    @JsonView(Views.ListView.class)
    LanguageCode code;
    @JsonView(Views.ListView.class)
    EnumMap<LanguageCode, String> localizedName;
    @JsonView(Views.ListView.class)
    int position;


}
