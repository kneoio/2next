package com.semantyca.mixpla.model.filter;

import com.semantyca.officeframe.model.cnst.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BrandAgentStatsFilter implements IFilter {
    private boolean activated = false;
    private String stationName;
    private CountryCode countryCode;
    private String streamType;

    @Override
    public boolean isActivated() {
        if (activated) return true;
        return hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        return (stationName != null && !stationName.isEmpty())
                || countryCode != null
                || streamType != null;
    }
}
