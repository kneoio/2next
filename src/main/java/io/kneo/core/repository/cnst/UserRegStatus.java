package io.kneo.core.repository.cnst;

import lombok.Getter;

public enum UserRegStatus {
    NO_ACCESS(0,"restricted"),
    BANNED(1,"banned"),
    WAITING_FOR_REG_CODE_CONFIRMATION(2, "wfrcc"),
    REGISTERED(3,"registered"),
    SUSPEND(4,"suspend"),
    REGISTERED_AUTOMATICALLY(5,"autoreg");
    private int code;
    @Getter
    private final String alias;

    UserRegStatus(int code, String alias) {
        this.code = code;
        this.alias = alias;
    }

    public static UserRegStatus getType(int code) {
        for (UserRegStatus type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return NO_ACCESS;
    }
    UserRegStatus(String alias) {
        this.alias = alias;
    }

}
