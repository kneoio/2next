package com.semantyca.core.model.scheduler;

import com.semantyca.core.model.cnst.TriggerType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class Task {
    private UUID id;
    private TriggerType triggerType;
    private OnceTrigger onceTrigger;
    private TimeWindowTrigger timeWindowTrigger;
    private PeriodicTrigger periodicTrigger;
}