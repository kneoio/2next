package com.semantyca.mixpla.model;

import com.semantyca.mixpla.model.cnst.ExpirationType;
import com.semantyca.mixpla.model.cnst.SceneTimingMode;
import com.semantyca.mixpla.model.cnst.SceneType;
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
public class Scene extends SecureDataEntity<UUID> {
    private UUID scriptId;
    private String title;
    private String scriptTitle;
    private SceneTimingMode timingMode;
    private List<ScenePrompt> introPrompts;
    private List<CustomAction> actions;
    private PlaylistRequest playlistRequest;
    private List<LocalTime> startTime;
    private int durationSeconds;
    private int seqNum;
    private SceneType sceneType;
    private boolean oneTimeRun;
    private boolean allowJingles;
    private boolean allowAds;
    private double talkativity = 0.5;
    private List<Integer> weekdays;
    private List<UUID> soundFragmentIds;
    private ExpirationType expiration;
    private int expiredAfterMinutes;
    private List<UUID> labels;

    /**
     * Defaults to LOOP. Falls back to the legacy {@code oneTimeRun} flag when not set,
     * so scenes persisted before sceneType existed still classify correctly.
     */
    public SceneType getSceneType() {
        if (sceneType != null) {
            return sceneType;
        }
        return oneTimeRun ? SceneType.ONE_TIME : SceneType.LOOP;
    }
}
