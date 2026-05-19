package com.semantyca.core.model.embedded;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
public class DocumentAccessInfo {
    private Long userId;
    private OffsetDateTime readingTime;
    private Boolean canEdit;
    private Boolean canDelete;
    private String userLogin;
    private boolean IsSu;
}
