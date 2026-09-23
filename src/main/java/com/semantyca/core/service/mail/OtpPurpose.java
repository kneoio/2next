package com.semantyca.core.service.mail;

import lombok.Getter;

@Getter
public enum OtpPurpose {
    signIn("otp.signIn"),
    uploadConfirmation("otp.uploadConfirmation");

    private final String messageKeyPrefix;

    OtpPurpose(String messageKeyPrefix) {
        this.messageKeyPrefix = messageKeyPrefix;
    }
}
