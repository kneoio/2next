package io.kneo.core.model.user;

import io.kneo.core.server.EnvConst;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IUser {
    Long getId();

    String getUserName();

    default Integer getPageSize(){
        return EnvConst.DEFAULT_PAGE_SIZE;
    }
    String getEmail();

    List<IRole> getActivatedRoles();

    boolean isSupervisor();

    void setSupervisor(boolean supervisor);

    @Deprecated
    default boolean isActive(){
        return true;
    }

    @NotNull String getLogin();
}