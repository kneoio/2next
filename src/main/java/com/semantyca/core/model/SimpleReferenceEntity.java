package com.semantyca.core.model;

import com.semantyca.core.model.cnst.LanguageCode;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.UUID;

@Setter
public class SimpleReferenceEntity extends DataEntity<UUID> {
    @Getter
    protected String identifier;
    @Getter
    protected EnumMap<LanguageCode, String> localizedName = new EnumMap<>(LanguageCode.class);
    protected Integer archived;

    public String getLocalizedName(LanguageCode lang) {
        try {
            String val = localizedName.get(lang);
            if (val != null && !val.isEmpty()) {
                return val;
            } else {
                return identifier;
            }
        } catch (Exception e) {
            return identifier;
        }
    }
}
