package com.semantyca.mixpla.dto.queue;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SongInfoDTO {
    private UUID songId;
    private int sequenceNumber;
    private int durationSeconds;

    public SongInfoDTO(UUID songId, int sequenceNumber, int durationSeconds) {
        this.songId = songId;
        this.sequenceNumber = sequenceNumber;
        this.durationSeconds = durationSeconds;
    }
}
