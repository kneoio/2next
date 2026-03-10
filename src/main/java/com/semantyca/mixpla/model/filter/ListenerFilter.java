package com.semantyca.mixpla.model.filter;

import io.kneo.officeframe.cnst.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ListenerFilter implements IFilter{
    private boolean activated = false;
    private List<CountryCode> countries;
    private String searchTerm;

    public boolean isActivated() {
        if (activated) {
            return true;
        }
        return hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        if (countries != null && !countries.isEmpty()) {
            return true;
        }
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return true;
        }
        return false;
    }
}