package com.semantyca.mixpla.model.stream;

import com.semantyca.core.model.cnst.LanguageTag;
import com.semantyca.mixpla.model.brand.AiOverriding;
import com.semantyca.mixpla.model.brand.BrandScriptEntry;
import com.semantyca.mixpla.model.brand.ProfileOverriding;
import com.semantyca.mixpla.model.cnst.AiAgentStatus;
import com.semantyca.mixpla.model.cnst.ManagedBy;
import com.semantyca.mixpla.model.cnst.StreamStatus;
import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.officeframe.model.cnst.CountryCode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public interface IStream {

    UUID getId();

    UUID getMasterBrandId();

    String getSlugName();

    EnumMap<LanguageCode, String> getLocalizedName();

    ZoneId getTimeZone();

    long getBitRate();

    ManagedBy getManagedBy();

    StreamStatus getStatus();

    void setStatus(StreamStatus status);

    IStreamer getStreamer();

    AiAgentStatus getAiAgentStatus();

    void setAiAgentStatus(AiAgentStatus currentAiStatus);

    CountryCode getCountry();

    UUID getAiAgentId();

    AiOverriding getAiOverriding();

    String getColor();

    default String getDescription() {
        return "";
    }

    void setColor(String s);

    void setPopularityRate(double popularityRate);

    void setAiAgentId(UUID aiAgentId);

    void setProfileId(UUID uuid);

    void setAiOverriding(AiOverriding aiOverriding);

    void setCountry(CountryCode country);

    void setScripts(List<BrandScriptEntry> brandScriptEntries);

    LocalDateTime getStartTime();

    UUID getProfileId();

    ProfileOverriding getProfileOverriding();

    double getPopularityRate();

    void setLastAgentContactAt(long l);

    long getLastAgentContactAt();

    LanguageTag getStreamLanguage();

    void setStreamLanguage(LanguageTag streamLanguage);

    boolean isActive();
}
