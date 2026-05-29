package com.semantyca.mixpla.model;

import com.semantyca.core.model.DataEntity;
import com.semantyca.core.model.UserData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class UserAd extends DataEntity<UUID> {
    private long userId;
    private UUID brandId;
    private String title;
    private String slugName;
    private String description;
    private String contacts;
    private UserData userData;
    private List<PlayHistory> playHistory;
}
