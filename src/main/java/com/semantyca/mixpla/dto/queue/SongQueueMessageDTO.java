package com.semantyca.mixpla.dto.queue;

import com.semantyca.mixpla.model.cnst.MergingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SongQueueMessageDTO {
    private UUID messageId;
    private int sequenceNumber;
    private String brandSlug;
    private MergingType mergingMethod;
    private UUID sceneId;
    private String sceneTitle;
    private Map<IntroKey, IntroInfoDTO> filePaths;
    private Map<SongKey, SongInfoDTO> songs;
    private int priority = 100;
}
