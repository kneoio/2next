package com.semantyca.mixpla.model.filter;

import com.semantyca.officeframe.model.cnst.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class ListenerFilter implements IFilter{
    private boolean activated = false;
    private List<CountryCode> countries;
    private String searchTerm;
    private List<UUID> listenerOf;

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
        if (listenerOf != null && !listenerOf.isEmpty()) {
            return true;
        }
        return searchTerm != null && !searchTerm.isEmpty();
    }
}