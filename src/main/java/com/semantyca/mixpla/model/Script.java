package com.semantyca.mixpla.model;

import com.semantyca.core.model.ScriptVariable;
import com.semantyca.core.model.SecureDataEntity;
import com.semantyca.core.model.cnst.LanguageTag;
import com.semantyca.mixpla.model.cnst.SceneTimingMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class Script extends SecureDataEntity<UUID> {
    private String name;
    private String slugName;
    private UUID defaultProfileId;
    private String description;
    private boolean custom;
    private List<UUID> labels;
    private boolean enabled;
    private LanguageTag languageTag;
    private SceneTimingMode timingMode;
    private List<ScriptVariable> requiredVariables;
}
