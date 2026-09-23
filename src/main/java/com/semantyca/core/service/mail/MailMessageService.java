package com.semantyca.core.service.mail;

import com.semantyca.core.model.cnst.LanguageCode;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class MailMessageService {

    private static final Logger LOG = Logger.getLogger(MailMessageService.class);
    private static final LanguageCode DEFAULT_LANGUAGE = LanguageCode.en;

    private final ConcurrentHashMap<LanguageCode, Properties> cache = new ConcurrentHashMap<>();

    public String get(LanguageCode language, String key) {
        String value = bundle(normalize(language)).getProperty(key);
        if (value == null) {
            value = bundle(DEFAULT_LANGUAGE).getProperty(key);
        }
        if (value == null) {
            throw new IllegalStateException("Missing mail message key '" + key + "'");
        }
        return value;
    }

    public Locale toLocale(LanguageCode language) {
        return Locale.forLanguageTag(normalize(language).getAltCode());
    }

    private LanguageCode normalize(LanguageCode language) {
        return language == null || language == LanguageCode.unknown ? DEFAULT_LANGUAGE : language;
    }

    private Properties bundle(LanguageCode language) {
        return cache.computeIfAbsent(language, this::load);
    }

    private Properties load(LanguageCode language) {
        String path = language == DEFAULT_LANGUAGE
                ? "mail/messages.properties"
                : "mail/messages_" + language.getAltCode() + ".properties";
        Properties properties = new Properties();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                LOG.warnf("No mail messages for language %s, using %s", language, DEFAULT_LANGUAGE);
                return properties;
            }
            properties.load(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + path, e);
        }
        return properties;
    }
}
