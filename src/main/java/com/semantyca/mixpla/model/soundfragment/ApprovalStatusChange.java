package com.semantyca.mixpla.model.soundfragment;


import com.semantyca.core.model.cnst.ApprovalStatus;

import java.time.LocalDateTime;

public record ApprovalStatusChange(LocalDateTime timestamp, ApprovalStatus oldStatus,
                                   ApprovalStatus newStatus) {
}
