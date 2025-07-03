package io.kneo.core.dto.validation;

import io.kneo.officeframe.cnst.CountryCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidCountryValidator implements ConstraintValidator<ValidCountry, CountryCode> {
    private Set<String> validCountryCodes;

    @Override
    public void initialize(ValidCountry annotation) {
        validCountryCodes = Arrays.stream(CountryCode.values())
                .map(CountryCode::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(CountryCode value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return validCountryCodes.contains(value.name());
    }
}