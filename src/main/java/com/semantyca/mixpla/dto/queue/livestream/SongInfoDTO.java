package com.semantyca.mixpla.dto.queue.livestream;

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

    public SongInfoDTO(UUID songId, int durationSeconds) {
        this.songId = songId;
        this.durationSeconds = durationSeconds;
    }
}