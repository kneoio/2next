package com.semantyca.mixpla.model.stream;

import com.semantyca.core.model.SecureDataEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class OtsDefinition extends SecureDataEntity<UUID> {
    private String slugName;
    private String name;
    private UUID scriptId;
    private Map<String, Object> userVariables;
    private UUID brandId;
    private UUID agentId;
}
