package com.semantyca.mixpla.model.soundfragment;

import com.semantyca.core.model.FileMetadata;
import com.semantyca.core.model.SecureDataEntity;
import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.mixpla.model.cnst.PlaylistItemType;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SharedSoundFragment  extends SecureDataEntity<UUID> {
    private UUID id;
    private OffsetDateTime regDate;
    private OffsetDateTime lastModDate;
    private Long sourceUserId;
    private String sourceUserName;
    private String sourceUserEmail;
    private UUID targetBrandId;
    private UUID soundFragmentId;
    private OffsetDateTime expiresAt;
    private Integer playedCount;
    private Integer ratedCount;
    private Integer boost;
    private Integer status;
    private Integer archived;
    private String brandSlugName;
    private String title;
    private String artist;
    private PlaylistItemType type;
    private String album;
    private List<UUID> genres;
    private List<UUID> labels;
    private EnumMap<LanguageCode, String> targetBrandName;
    private List<FileMetadata> fileMetadataList;
}
