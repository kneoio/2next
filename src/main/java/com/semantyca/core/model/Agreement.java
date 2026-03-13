package com.semantyca.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.semantyca.officeframe.model.cnst.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class Agreement extends DataEntity<java.util.UUID> {
    private CountryCode country;
    private String userAgent;
    private String agreementVersion;
    private String termsText;
}
