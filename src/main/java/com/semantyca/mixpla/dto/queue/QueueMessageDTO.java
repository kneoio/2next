package com.semantyca.mixpla.dto.queue;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueueMessageDTO {
    private String brandName;
    private AddToQueueDTO dto;
    private String uploadId;
}
