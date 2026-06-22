package com.semantyca.mixpla.dto.playlist;

import java.util.List;
import java.util.UUID;

public class PlaylistQueueEntryDTO {

    @Deprecated
    public static class DjInfo {
        public String label;
        public String title;
        public String artist;
    }

    public static class SongInfo {
        public List<String> labels;
        public String title;
        public String artist;
    }

    public static class TechInfo {
        public int pos;
        public String queueType;
        public Integer priority;
        public UUID songId;
        public String slugName;
        public String mergingMethod;
        public Integer duration;
    }

    @Deprecated
    public SongInfo songInfo;
    public DjInfo dj;
    public TechInfo tech;
}
