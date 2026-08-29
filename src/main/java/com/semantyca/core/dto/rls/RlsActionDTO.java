package com.semantyca.core.dto.rls;

import com.semantyca.core.model.cnst.RlsActionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RlsActionDTO {
    private RlsActionType action;
    private Long userId;
    private boolean canEdit;
    private boolean canDelete;
}
