package com.semantyca.mixpla.model;

import java.time.OffsetDateTime;

public record PlayHistory(
        OffsetDateTime playedAt,
        Integer duration,
        String speechText,
        String djName
) {
}
