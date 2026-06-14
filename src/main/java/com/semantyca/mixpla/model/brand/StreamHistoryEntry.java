package com.semantyca.mixpla.model.brand;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StreamHistoryEntry {
    private String event;
    private ZonedDateTime ts;

    public StreamHistoryEntry(String event, ZonedDateTime ts) {
        this.event = event;
        this.ts = ts;
    }
}
