package io.kneo.core.model.embedded;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class DocumentAccessInfo {
    private Long userId;
    private LocalDateTime readingTime;
    private Boolean canEdit;
    private Boolean canDelete;
    private String userLogin;
    private boolean IsSu;
}
