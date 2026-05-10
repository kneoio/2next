package com.semantyca.mixpla.dto.playlist;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class PlaylistQueueStateDTO {
    public UUID brandId;
    public List<PlaylistQueueEntryDTO> fullQueue;
    public OffsetDateTime updatedAt;
}
