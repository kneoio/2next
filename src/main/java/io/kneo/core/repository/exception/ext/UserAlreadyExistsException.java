package io.kneo.core.repository.exception.ext;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException(String userLogin) {
        super("\"" + userLogin + "\" is exist");
    }

    public String getDeveloperMessage() {
        return getMessage();
    }
}
