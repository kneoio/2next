package com.semantyca.mixpla.model.brand;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StreamingOptions {
    private List<String> codecs = List.of("OPUS");
}
