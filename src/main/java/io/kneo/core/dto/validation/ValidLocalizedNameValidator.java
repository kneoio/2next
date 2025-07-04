package io.kneo.core.dto.validation;

import io.kneo.core.localization.LanguageCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.EnumMap;
import java.util.Map;

public class ValidLocalizedNameValidator implements ConstraintValidator<ValidLocalizedName, EnumMap<LanguageCode, String>> {
    private int minLength;
    private int maxLength;
    private boolean allowEmptyMap;
    private boolean requireDefaultLanguage;
    private LanguageCode defaultLanguage;

    @Override
    public void initialize(ValidLocalizedName annotation) {
        this.minLength = annotation.minLength();
        this.maxLength = annotation.maxLength();
        this.allowEmptyMap = annotation.allowEmptyMap();
        this.requireDefaultLanguage = annotation.requireDefaultLanguage();
        this.defaultLanguage = annotation.defaultLanguage();
    }

    @Override
    public boolean isValid(EnumMap<LanguageCode, String> localizedNameMap, ConstraintValidatorContext context) {
        if (localizedNameMap == null) {
            return true;
        }

        if (localizedNameMap.isEmpty()) {
            if (!allowEmptyMap) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Localized name must contain at least one language entry")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }

        if (requireDefaultLanguage) {
            if (!localizedNameMap.containsKey(defaultLanguage) ||
                    localizedNameMap.get(defaultLanguage) == null ||
                    localizedNameMap.get(defaultLanguage).trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Default language (" + defaultLanguage + ") name is required")
                        .addConstraintViolation();
                return false;
            }
        }

        for (Map.Entry<LanguageCode, String> entry : localizedNameMap.entrySet()) {
            String name = entry.getValue();
            LanguageCode language = entry.getKey();

            if (name == null || name.trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Name for language " + language + " cannot be empty")
                        .addConstraintViolation();
                return false;
            }

            String trimmedName = name.trim();
            if (trimmedName.length() < minLength || trimmedName.length() > maxLength) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "Name for language " + language + " must be between " + minLength + " and " + maxLength + " characters")
                        .addConstraintViolation();
                return false;
            }

            if (!trimmedName.matches("^[a-zA-Z0-9\\s\\-_.,!?()&]+$")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "Name for language " + language + " contains invalid characters")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}

