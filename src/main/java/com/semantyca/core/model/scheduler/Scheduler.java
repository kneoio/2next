package com.semantyca.core.model.scheduler;

import io.vertx.core.net.impl.pool.Task;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.util.List;

@Setter
@Getter
public class Scheduler {
    private boolean enabled;
    private ZoneId timeZone;
    private List<Task> tasks;
}