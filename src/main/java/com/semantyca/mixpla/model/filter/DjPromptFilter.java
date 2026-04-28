package com.semantyca.mixpla.model.filter;

import com.semantyca.core.model.cnst.LanguageTag;
import com.semantyca.mixpla.model.cnst.PromptType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class DjPromptFilter implements IFilter{
    private boolean activated = false;
    private LanguageTag languageTag;
    private PromptType promptType;
    private boolean enabled;
    private boolean master;
    private boolean locked;
    private List<UUID> labels;

    @Override
    public boolean isActivated() {
        return activated || hasAnyFilter();
    }

    @Override
    public boolean hasAnyFilter() {
        if (labels != null && !labels.isEmpty()) {
            return true;
        }

        return languageTag != null ||
                promptType != null ||
                enabled ||
                master ||
                locked;
    }
}