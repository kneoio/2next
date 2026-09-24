package com.semantyca.mixpla.dto.queue.livestream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * A human-rendered transition for DJ_JOIN, in up to two renders of the same link:
 * the full one (outgoing song + DJ lanes + incoming song) and plan B (DJ lanes + incoming song, outgoing muted),
 * which airs when the outgoing song has already played past the point the full one would be stitched in.
 * Outgoing and incoming songs travel in SongQueueMessageDTO.songs as SONG_1 and SONG_2. A join with no
 * outgoing song (the DJ's voice then the incoming song, e.g. right after the AI DJ handed over) has no
 * SONG_1 and only a plan B render. A closing link (the outgoing song, then the DJ's goodbye) has no SONG_2
 * and ends the chain: nothing continues from it.
 */
@Getter
@Setter
@NoArgsConstructor
public class DjJoinDTO {
    private UUID joinId;
    /** The previous join whose incoming song is this join's outgoing song; null for the first join of a session. */
    private UUID continuesJoinId;
    /** The full render; null when the outgoing song had already aired past the mix point when the DJ sent it. */
    private String filePath;
    private double durationSeconds;
    /** Where second 0 of the incoming song sits in the full render. */
    private double incomingSongStartSeconds;
    /** Where in the outgoing song the full render begins. */
    private double outgoingSongFromSeconds;
    /** Where in the full render the DJ's mix begins; everything before it is the outgoing song played plain. */
    private double mixPointSeconds;
    /** The render without the outgoing song; null for the first join of a session. */
    private String planBFilePath;
    /** Where second 0 of the incoming song sits in the plan B render. */
    private double planBIncomingSongStartSeconds;
}
