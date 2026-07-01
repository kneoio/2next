package com.semantyca.core.dto.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class UserDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    String author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    ZonedDateTime regDate;
    String lastModifier;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    protected ZonedDateTime lastModifiedDate;
    @NotNull
    String identifier;
    @NotNull
    String name;
    @NotNull
    String login;
    @NotNull @Email
    String email;
    String language;
    String theme;
    List<UUID> labels;
}
