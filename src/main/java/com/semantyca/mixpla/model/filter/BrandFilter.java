package com.semantyca.mixpla.model.filter;

import com.semantyca.officeframe.model.cnst.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class BrandFilter implements IFilter{
    private boolean activated = false;
    private List<CountryCode> countries;
    private boolean publicBrand;


    public boolean isActivated() {
        return activated || hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        if (countries != null && !countries.isEmpty()) {
            return true;
        }
        return publicBrand;
    }
}