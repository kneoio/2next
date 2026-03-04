package com.semantyca.core.model.scheduler;

import java.util.UUID;

public interface Schedulable {
    Scheduler getScheduler();
    UUID getId();

}