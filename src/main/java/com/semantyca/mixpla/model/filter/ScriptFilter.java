package com.semantyca.mixpla.model.filter;

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
public class ScriptFilter implements IFilter{
    private boolean activated = false;
    private List<UUID> labels;
    private SceneTimingMode timingMode;
    private LanguageTag languageTag;
    private String searchTerm;

    public boolean isActivated() {
        if (activated) {
            return true;
        }
        return hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        if (labels != null && !labels.isEmpty()) {
            return true;
        }
        if (timingMode != null) {
            return true;
        }
        if (languageTag != null) {
            return true;
        }
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }
}
