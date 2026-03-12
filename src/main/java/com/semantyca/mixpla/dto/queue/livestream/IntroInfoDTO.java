package com.semantyca.mixpla.dto.queue.livestream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IntroInfoDTO {
    private String filePath;
    private int sequenceNumber;
    private int durationSeconds;

    public IntroInfoDTO(String filePath, int durationSeconds) {
        this.filePath = filePath;
        this.durationSeconds = durationSeconds;
    }
}