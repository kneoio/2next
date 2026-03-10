package com.semantyca.mixpla.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.semantyca.mixpla.model.cnst.PlaylistItemType;
import com.semantyca.mixpla.model.cnst.SourceType;
import com.semantyca.mixpla.model.cnst.WayOfSourcing;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistRequest {
    private WayOfSourcing sourcing = WayOfSourcing.RANDOM;
    private String title = "";
    private String artist = "";
    private List<UUID> genres = List.of();
    private List<UUID> labels = List.of();
    private List<PlaylistItemType> type = List.of(PlaylistItemType.SONG);
    private List<SourceType> source = List.of(SourceType.USER_UPLOAD);
    private String searchTerm = "";
    private List<UUID> soundFragments = List.of();
    private List<ScenePrompt> contentPrompts = List.of();
}