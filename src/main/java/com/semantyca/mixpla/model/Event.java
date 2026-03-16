package com.semantyca.mixpla.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.semantyca.core.model.scheduler.Schedulable;
import com.semantyca.core.model.scheduler.Scheduler;
import com.semantyca.mixpla.model.cnst.EventPriority;
import com.semantyca.mixpla.model.cnst.EventType;
import com.semantyca.core.model.SecureDataEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event extends SecureDataEntity<UUID> implements Schedulable {
    private UUID brandId;
    private String brand;
    private ZoneId timeZone;
    private EventType type;
    private String description;
    private EventPriority priority;
    private PlaylistRequest playlistRequest;
    private Scheduler scheduler;
    private List<ScenePrompt> scenePrompts;

}