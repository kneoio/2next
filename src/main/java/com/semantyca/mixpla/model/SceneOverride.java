package com.semantyca.mixpla.model;

import com.semantyca.core.model.SecureDataEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class SceneOverride extends SecureDataEntity<UUID> {
    private String title;
    private UUID sceneId;
    private List<LocalTime> startTime;
    private List<Object> actionsData;
    private PlaylistRequest stagePlaylist;
    private List<Integer> weekdays;
}
