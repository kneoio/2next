package com.semantyca.mixpla.dto.playlist;

import java.util.UUID;

public class PlaylistQueueEntryDTO {

    public static class DjInfo {
        public String label;
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

    public DjInfo dj;
    public TechInfo tech;
}
