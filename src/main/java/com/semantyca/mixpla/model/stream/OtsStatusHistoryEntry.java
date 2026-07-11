package com.semantyca.mixpla.model.stream;

import com.semantyca.mixpla.model.cnst.OtsRunStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@NoArgsConstructor
public class OtsStatusHistoryEntry {
    private OtsRunStatus status;
    private ZonedDateTime timestamp;
}
