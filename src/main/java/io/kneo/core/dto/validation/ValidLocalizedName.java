package io.kneo.core.dto.validation;

import io.kneo.core.localization.LanguageCode;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidLocalizedNameValidator.class)
public @interface ValidLocalizedName {
    String message() default "Invalid localized name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int minLength() default 1;
    int maxLength() default 255;
    boolean allowEmptyMap() default false;
    boolean requireDefaultLanguage() default true;
    LanguageCode defaultLanguage() default LanguageCode.en;
}
