package com.semantyca.core.repository.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String userLogin) {
        super("\"" + userLogin + "\" not found");
    }

    public String getDeveloperMessage() {
        return getMessage();
    }
}
