package io.kneo.core.repository.exception;

public class UploadAbsenceException extends RuntimeException {

    public UploadAbsenceException(String msg) {
        super(msg);
    }

    public UploadAbsenceException(Throwable failure) {
        super(failure);
    }

    public UploadAbsenceException(String s, Exception e) {
        super(s, e);
    }

    public String getDeveloperMessage() {
        return getMessage();
    }
}
