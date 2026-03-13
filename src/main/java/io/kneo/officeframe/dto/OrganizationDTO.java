package io.kneo.officeframe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.semantyca.core.dto.AbstractReferenceDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class OrganizationDTO extends AbstractReferenceDTO {
    private OrgCategoryDTO orgCategory;
    private boolean isPrimary;
    private String bizID;
    private int rank;
}
