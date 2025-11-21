package io.kneo.core.dto.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.kneo.core.dto.AbstractDTO;
import io.kneo.core.dto.Views;
import io.kneo.officeframe.cnst.CountryCode;
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
