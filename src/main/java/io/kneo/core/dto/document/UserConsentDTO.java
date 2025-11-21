package io.kneo.core.dto.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.kneo.core.dto.AbstractDTO;
import io.kneo.core.dto.Views;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class UserConsentDTO extends AbstractDTO {
    @JsonView(Views.DetailView.class)
    private String userId;
    @JsonView(Views.DetailView.class)
    private boolean essential;
    @JsonView(Views.DetailView.class)
    private boolean analytics;
    @JsonView(Views.DetailView.class)
    private boolean marketing;
    @JsonView(Views.DetailView.class)
    private ZonedDateTime timestamp;
    private String ipAddress;
    private String userAgent;
}
