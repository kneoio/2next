package com.semantyca.core.model.user;

public class Bot extends SystemAbstractUser {
    public final static String USER_NAME = "bot";
    public final static long ID = 2;

    public Long getId() {
        return ID;
    }

    @Override
    public String getUserName() {
        return USER_NAME;
    }

    @Override
    public String getLogin() {
        return getUserName();
    }

    public static Bot build() {
        return new Bot();
    }

}
