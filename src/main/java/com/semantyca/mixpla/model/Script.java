package com.semantyca.mixpla.model;

import com.semantyca.core.model.ScriptVariable;
import com.semantyca.core.model.cnst.LanguageTag;
import com.semantyca.mixpla.model.cnst.SceneTimingMode;
import com.semantyca.core.model.SecureDataEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class Script extends SecureDataEntity<UUID> {
    private String name;
    private String slugName;
    private UUID defaultProfileId;
    private String description;
    private Integer accessLevel = 0;
    private List<UUID> labels;
    private List<UUID> brands;
    @Deprecated
    private NavigableSet<Scene> scenes =
            new TreeSet<>(Comparator
                    .comparingInt(Scene::getSeqNum)
                    .thenComparing(Scene::getId));
    private boolean enabled;
    private LanguageTag languageTag;
    private SceneTimingMode timingMode;
    private List<ScriptVariable> requiredVariables;
}
