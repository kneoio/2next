package io.kneo.core.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DocumentAccessDTO {
    private Long userId;
    private Boolean canEdit;
    private Boolean canDelete;
    private String userLogin;
    private boolean IsSu;
}