package com.semantyca.mixpla.model.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class OtsDefinitionFilter implements IFilter {
    private boolean activated = false;
    private UUID brandId;
    private String searchTerm;

    public boolean isActivated() {
        return activated || hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        if (brandId != null) {
            return true;
        }
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }
}
