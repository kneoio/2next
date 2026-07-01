package com.semantyca.core.model.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserFilter {
    private String searchTerm;

    public boolean isActivated() {
        return hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }
}
