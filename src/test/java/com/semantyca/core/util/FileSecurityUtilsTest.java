package com.semantyca.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSecurityUtilsTest {

    @Test
    void sanitizeFilename_norwegianLetters_transliteratedToAscii() {
        String sanitized = FileSecurityUtils.sanitizeFilename("Blåbærsyltetøy på æblet ØÆÅ.wav");

        assertTrue(sanitized.matches("[\\x20-\\x7E]+"), "sanitized name must be pure ASCII: " + sanitized);
        assertTrue(sanitized.endsWith(".wav"));
        assertEquals("Blabaersyltetoy pa aeblet OAEA.wav", sanitized);
    }

    @Test
    void sanitizeFilename_accentedLatinAndOtherNonAscii_fallBackToUnderscore() {
        String sanitized = FileSecurityUtils.sanitizeFilename("Café – 你好.mp3");

        assertTrue(sanitized.matches("[\\x20-\\x7E]+"), "sanitized name must be pure ASCII: " + sanitized);
        assertTrue(sanitized.endsWith(".mp3"));
    }
}
