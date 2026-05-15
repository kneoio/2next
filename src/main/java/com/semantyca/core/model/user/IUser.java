package com.semantyca.core.model.user;

import com.semantyca.core.server.EnvConst;
import jakarta.validation.constraints.NotNull;

public interface IUser {
    Long getId();

    String getUserName();

    default Integer getPageSize(){
        return EnvConst.DEFAULT_PAGE_SIZE;
    }
    String getEmail();

    boolean isSupervisor();

    void setSupervisor(boolean supervisor);

    @Deprecated
    default boolean isActive(){
        return true;
    }

    @NotNull String getLogin();
}