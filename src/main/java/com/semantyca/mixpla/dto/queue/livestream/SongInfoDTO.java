package com.semantyca.mixpla.dto.queue.livestream;

import com.semantyca.mixpla.model.cnst.SourceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SongInfoDTO {
    private UUID songId;
    private int durationSeconds;
    private SourceType sourceType;
    private String streamUrl;
    private String overrideTitle;
    private String overrideArtist;

    public SongInfoDTO(UUID songId, int durationSeconds) {
        this.songId = songId;
        this.durationSeconds = durationSeconds;
    }
}