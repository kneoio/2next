package com.semantyca.mixpla.model.brand;

import com.semantyca.core.model.FileMetadata;
import com.semantyca.core.model.SecureDataEntity;
import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.mixpla.model.cnst.ChatFeatureFlag;
import com.semantyca.mixpla.model.cnst.ManagedBy;
import com.semantyca.mixpla.model.cnst.SubmissionPolicy;
import com.semantyca.officeframe.model.Label;
import com.semantyca.officeframe.model.cnst.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class Brand extends SecureDataEntity<UUID> {
    private EnumMap<LanguageCode, String> localizedName = new EnumMap<>(LanguageCode.class);
    private String slugName;
    private ZoneId timeZone;
    private Integer isTemporary = 0;
    private int publicBrand;
    private CountryCode country;
    private long bitRate;
    private ManagedBy managedBy = ManagedBy.ITSELF;
    private String color;
    private String description;
    private String titleFont;
    private double popularityRate;
    private UUID aiAgentId;
    private UUID profileId;
    private AiOverriding aiOverriding;
    private ProfileOverriding profileOverriding;
    private SubmissionPolicy oneTimeStreamPolicy = SubmissionPolicy.NOT_ALLOWED;
    private SubmissionPolicy submissionPolicy = SubmissionPolicy.NOT_ALLOWED;
    private SubmissionPolicy messagingPolicy = SubmissionPolicy.REVIEW_REQUIRED;
    private Map<ChatFeatureFlag, Boolean> chatFeatureFlags = new HashMap<>();
    private List<Label> labelList;
    private List<BrandScriptEntry> scriptIds;
    private UUID customScriptId;
    private String scriptMode = "PREDEFINED";
    private Owner owner;
    private List<UUID> labels;
    private List<UUID> genres;
    private List<FileMetadata> fileMetadataList = new ArrayList<>();
    private StreamingOptions streamingOptions;
    private List<StreamHistoryEntry> streamHistory = new ArrayList<>();

    public String toString() {
        return String.format("id: %s, slug: %s", getId(), slugName);
    }

}