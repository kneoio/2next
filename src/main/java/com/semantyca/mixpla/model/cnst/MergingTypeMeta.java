package com.semantyca.mixpla.model.cnst;

import com.semantyca.mixpla.dto.queue.livestream.SongKey;

import java.util.Map;
import java.util.Set;

/**
 * Static metadata companion for {@link MixingType}.
 *
 * <p>Centralises timing constants so that duration estimates in timeline
 * builders and song-selection budgeting all derive from the same numbers.
 */
public final class MergingTypeMeta {

    public static final int AVERAGE_INTRO_DURATION_SECONDS = 10;
    public static final int AVERAGE_JINGLE_DURATION_SECONDS = 5;
    public static final int CROSSFADE_OVERLAP_SECONDS = 0;
    public static final int AVERAGE_GENERATED_CONTENT_DURATION_SECONDS = 60;

    /**
     * Per-type descriptor.
     *
     * @param songsPerEntry       how many songs the strategy consumes (1 or 2)
     * @param introCount          number of TTS intros generated (0, 1, or 2)
     * @param hasJingle           whether a jingle is appended to the entry
     * @param hasCrossfade        whether the entry overlaps the next
     * @param hasBackground       whether a background music track is required
     * @param hasGeneratedContent whether a generated content track (news/ad) is required
     * @param requiredSongKeys    artifact slots that must be supplied for this mixing type
     */
    public record Info(int songsPerEntry, int introCount, boolean hasJingle, boolean hasCrossfade,
                       boolean hasBackground, boolean hasGeneratedContent,
                       Set<SongKey> requiredSongKeys) {

        public int audioOverheadSeconds() {
            int overhead = introCount * AVERAGE_INTRO_DURATION_SECONDS;
            if (hasJingle) overhead += AVERAGE_JINGLE_DURATION_SECONDS;
            return overhead;
        }

        public int crossfadeOverlapSeconds() {
            return hasCrossfade ? CROSSFADE_OVERLAP_SECONDS : 0;
        }
    }

    private static final Map<MixingType, Info> META = Map.ofEntries(
            Map.entry(MixingType.INTRO_SONG,                                      new Info(1, 1, false, false, false, false, Set.of(SongKey.SONG_1))),
            Map.entry(MixingType.LISTENER_INTRO_SONG,                             new Info(1, 1, false, false, false, false, Set.of(SongKey.SONG_1))),
            Map.entry(MixingType.NOT_MIXED,                                       new Info(1, 0, false, false, false, false, Set.of(SongKey.SONG_1))),
            Map.entry(MixingType.SONG_ONLY,                                       new Info(1, 0, false, false, false, false, Set.of(SongKey.SONG_1))),
            Map.entry(MixingType.SONG_INTRO_SONG,                                 new Info(2, 1, false, false, false, false, Set.of(SongKey.SONG_1, SongKey.SONG_2))),
            Map.entry(MixingType.FILLER_JINGLE,                                   new Info(1, 0, true,  false, false, false, Set.of(SongKey.SONG_1, SongKey.JINGLE_1))),
            Map.entry(MixingType.JINGLE_INTRO_SONG,                               new Info(1, 1, true,  false, false, false, Set.of(SongKey.SONG_1, SongKey.JINGLE_1))),
            Map.entry(MixingType.INTRO_SONG_INTRO_SONG,                           new Info(2, 2, false, false, false, false, Set.of(SongKey.SONG_1, SongKey.SONG_2))),
            Map.entry(MixingType.SONG_CROSSFADE_SONG,                             new Info(2, 0, false, true,  false, false, Set.of(SongKey.SONG_1, SongKey.SONG_2))),
            Map.entry(MixingType.JINGLE_GENERATED_JINGLE_WITH_BACKGROUND,         new Info(1, 0, true,  false, true,  true,  Set.of(SongKey.JINGLE_INTRO, SongKey.JINGLE_OUTRO, SongKey.GENERATED_CONTENT, SongKey.BACKGROUND_LOOP))),
            Map.entry(MixingType.JINGLE_GENERATED_JINGLE,                         new Info(1, 0, true,  false, false, true,  Set.of(SongKey.JINGLE_INTRO, SongKey.JINGLE_OUTRO, SongKey.GENERATED_CONTENT))),
            Map.entry(MixingType.INTRO_JINGLE_GENERATED_JINGLE_WITH_BACKGROUND,   new Info(1, 1, true,  false, true,  true,  Set.of(SongKey.JINGLE_INTRO, SongKey.JINGLE_OUTRO, SongKey.GENERATED_CONTENT, SongKey.BACKGROUND_LOOP)))
    );

    public static Info of(MixingType type) {
        return META.getOrDefault(type, new Info(1, 0, false, false, false, false, Set.of()));
    }

    /**
     * Expected average overhead per song (seconds) as a continuous function of {@code talkativity}.
     */
    public static double averageOverheadPerSong(double talkativity) {
        return 1.5 + talkativity * 9.75;
    }

    private MergingTypeMeta() {}
}
