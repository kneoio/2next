package com.semantyca.core.dto;

import com.semantyca.core.model.cnst.SSEProgressStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SSEProgressDTO {
    private String id;
    private String name;
    private SSEProgressStatus status;
    private String errorMessage;
}