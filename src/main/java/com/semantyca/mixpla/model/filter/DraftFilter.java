package com.semantyca.mixpla.model.filter;

import com.semantyca.core.model.cnst.LanguageTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DraftFilter implements IFilter{
    private boolean activated = false;

    private LanguageTag languageTag;
    private boolean enabled;

    public boolean isActivated() {
        return activated || hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        return languageTag != null ||
               enabled;
    }
}