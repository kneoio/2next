package com.semantyca.mixpla.model.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserAdFilter implements IFilter {
    private boolean activated = false;
    private String searchTerm;
    private Long userId;

    @Override
    public boolean isActivated() {
        if (activated) {
            return true;
        }
        return hasAnyFilter();
    }

    @Override
    public boolean hasAnyFilter() {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return true;
        }
        return userId != null;
    }
}
