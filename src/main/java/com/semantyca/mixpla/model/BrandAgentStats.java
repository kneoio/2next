package com.semantyca.mixpla.model;

import com.semantyca.mixpla.model.cnst.StreamType;
import com.semantyca.officeframe.model.cnst.CountryCode;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class BrandAgentStats {
    private Integer id;
    private String stationName;
    private String userAgent;
    private String ipAddress;
    private CountryCode countryCode;
    private Long accessCount;
    private StreamType streamType;
    private OffsetDateTime lastAccessTime;
}
