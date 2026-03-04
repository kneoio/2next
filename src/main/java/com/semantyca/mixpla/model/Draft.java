package com.semantyca.mixpla.model;

import com.semantyca.core.model.cnst.LanguageTag;
import com.semantyca.mixpla.model.cnst.DraftingMethod;
import io.kneo.core.model.DataEntity;
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
    private LanguageTag languageTag;
    private DraftingMethod method;
    private Integer archived;
    private boolean enabled;
    private boolean isMaster;
    private boolean locked;
    private UUID masterId;
    private double version;
}
