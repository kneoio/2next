package kneo.core.model.user;

public class UndefinedUser extends SystemAbstractUser {
    public final static String USER_NAME = "undefined";
    public final static long ID = -999;

    public Long getId() {
        return ID;
    }

    public String getUserName() {
        return USER_NAME;
    }

    public static IUser Build() {
        return new UndefinedUser();
    }

}
