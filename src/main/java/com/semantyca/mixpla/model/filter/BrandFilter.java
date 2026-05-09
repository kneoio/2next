package com.semantyca.mixpla.model.filter;

import com.semantyca.mixpla.model.cnst.SubmissionPolicy;
import com.semantyca.officeframe.model.cnst.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class BrandFilter implements IFilter{
    private boolean activated = false;
    private List<CountryCode> countries;
    private boolean publicBrand;
    private List<UUID> labels;
    private SubmissionPolicy oneTimeStreamPolicy;
    private SubmissionPolicy submissionPolicy;
    private SubmissionPolicy messagingPolicy;


    public boolean isActivated() {
        return activated || hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        if (labels != null && !labels.isEmpty()) {
            return true;
        }

        if (countries != null && !countries.isEmpty()) {
            return true;
        }
        if (oneTimeStreamPolicy != null || submissionPolicy != null || messagingPolicy != null) {
            return true;
        }
        return publicBrand;
    }
}