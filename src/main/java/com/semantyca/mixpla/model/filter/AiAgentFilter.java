package com.semantyca.mixpla.model.filter;

import com.semantyca.core.model.cnst.LanguageTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class AiAgentFilter implements IFilter {
    private boolean activated = false;
    private List<UUID> labels;
    private String searchTerm;
    private LanguageTag languageTag;

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
        if (languageTag != null) {
            return true;
        }
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }
}
