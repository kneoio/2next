package com.semantyca.core.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semantyca.core.model.DataEntity;
import com.semantyca.core.repository.cnst.UserRegStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.TimeZone;

@Setter
@Getter
@NoArgsConstructor
public class User extends DataEntity<Long> implements IUser {
    @NotBlank
    private String login;
    @JsonIgnore
    @NotBlank
    private String email;
    private boolean isSupervisor;
    private Integer defaultLang;
    private TimeZone timeZone;
    private UserRegStatus regStatus;
    private int confirmationCode;

    @Override
    public String getUserName() {
        return login;
    }
    @Override
    public Integer getPageSize() {
        return IUser.super.getPageSize();
    }

}
