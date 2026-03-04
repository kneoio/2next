package com.semantyca.mixpla.model;

import io.kneo.core.dto.actions.cnst.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScenePrompt {
    private ActionType actionType;
    private UUID promptId;
    private boolean active = true;
    private int rank = 0;
    private BigDecimal weight = BigDecimal.valueOf(0.5);
}
