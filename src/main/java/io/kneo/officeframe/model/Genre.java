package io.kneo.officeframe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kneo.core.model.SimpleReferenceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class Genre extends SimpleReferenceEntity {
    private Integer rank = 999;
    private String color;
    private String fontColor;
    private UUID parent;
}
