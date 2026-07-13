package com.semantyca.mixpla.model.stream;

import com.semantyca.core.model.SecureDataEntity;
import com.semantyca.mixpla.model.cnst.OtsRunStatus;
import com.semantyca.mixpla.model.cnst.OtsRunType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
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
    private OtsRunStatus status;
    private List<OtsStatusHistoryEntry> statusHistory;
    private OtsRunType type;
    private Integer estimatedDurationMin;
    private String chatContext;
}
