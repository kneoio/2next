package com.semantyca.mixpla.model.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MixplaUserSubscriptionFilter implements IFilter {
    private boolean activated = false;
    private Long userId;
    private String subscriptionStatus;
    private String subscriptionType;
    private Boolean active;

    @Override
    public boolean isActivated() {
        if (activated) return true;
        return hasAnyFilter();
    }

    @Override
    public boolean hasAnyFilter() {
        if (userId != null) return true;
        if (subscriptionStatus != null && !subscriptionStatus.isEmpty()) return true;
        if (subscriptionType != null && !subscriptionType.isEmpty()) return true;
        return active != null;
    }
}
