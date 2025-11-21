package io.kneo.core.dto.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class UserDTO {
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
    @NotNull
    String language;
    @NotNull
    String theme;
    List<String> roles = Collections.emptyList();
    List<String> modules = Collections.emptyList();
}
