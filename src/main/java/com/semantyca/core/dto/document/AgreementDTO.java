package com.semantyca.core.dto.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.semantyca.officeframe.model.cnst.CountryCode;
import com.semantyca.core.dto.AbstractDTO;
import com.semantyca.core.dto.Views;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class AgreementDTO extends AbstractDTO {
    @JsonView(Views.DetailView.class)
    private CountryCode country;
    private String userAgent;
    @JsonView(Views.DetailView.class)
    private String agreementVersion;
    @JsonView(Views.DetailView.class)
    private String termsText;
}
