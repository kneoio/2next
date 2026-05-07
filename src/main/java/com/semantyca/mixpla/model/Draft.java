package com.semantyca.mixpla.model;

import com.semantyca.core.model.DataEntity;
import com.semantyca.mixpla.model.cnst.DraftingMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class Draft extends DataEntity<UUID> {
    private String draftType;
    private String title;
    private String content;
    private String description;
    private DraftingMethod method;
    private boolean enabled;
    private double version;
}
