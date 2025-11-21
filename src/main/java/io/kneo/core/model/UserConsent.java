package io.kneo.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class UserConsent extends DataEntity<java.util.UUID> {
    private String userId;
    private boolean essential = true;
    private boolean analytics = false;
    private boolean marketing = false;
    private ZonedDateTime timestamp;
    private String ipAddress;
    private String userAgent;
}
