package com.semantyca.mixpla.model;

import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.core.model.cnst.LanguageTag;
import com.semantyca.mixpla.model.cnst.PromptType;
import com.semantyca.core.model.SecureDataEntity;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DjPrompt extends SecureDataEntity<UUID> {
    private String slugName;
    private boolean enabled;
    private String prompt;
    private String description;
    private PromptType promptType;  //used only for UI to filter in different form
    private LanguageTag languageTag;
    private boolean master;
    private boolean locked;
    private String title;
    private JsonObject backup;
    private UUID draftId;
    private UUID masterId;
    private double version;
    private int allowAsOption;
    @Deprecated
    private JsonObject optionLocName;
    private EnumMap<LanguageCode, String> localizedOptionName = new EnumMap<>(LanguageCode.class);
    private JsonArray exposedVariables;
    private List<UUID> labels;

}
