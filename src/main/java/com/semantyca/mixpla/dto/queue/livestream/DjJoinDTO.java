package com.semantyca.mixpla.dto.queue.livestream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * A human-rendered transition file (outgoing song tail + DJ lanes + incoming song) for DJ_JOIN.
 * Outgoing and incoming songs travel in SongQueueMessageDTO.songs as SONG_1 and SONG_2.
 */
@Getter
@Setter
@NoArgsConstructor
public class DjJoinDTO {
    private UUID joinId;
    /** The previous join whose incoming song is this join's outgoing song; null for the first join of a session. */
    private UUID continuesJoinId;
    private String filePath;
    private double durationSeconds;
    /** Where second 0 of the incoming song sits in this file. */
    private double incomingSongStartSeconds;
    /** Where in the outgoing song this file begins; the cut point into the previous join. */
    private double outgoingSongFromSeconds;
}
