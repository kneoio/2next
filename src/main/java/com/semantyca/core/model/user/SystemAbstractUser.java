package com.semantyca.core.model.user;

import java.util.TimeZone;

public abstract class SystemAbstractUser implements IUser {

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    @Override
    public Integer getPageSize() {
        return 20;
    }

    @Override
    public boolean isSupervisor() {
        return false;
    }

    @Override
    public void setSupervisor(boolean supervisor) {

    }


}
