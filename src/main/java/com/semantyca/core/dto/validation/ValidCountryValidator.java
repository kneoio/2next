package com.semantyca.core.dto.validation;

import com.semantyca.officeframe.model.cnst.CountryCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidCountryValidator implements ConstraintValidator<ValidCountry, String> {
    private Set<String> validCountryCodes;

    @Override
    public void initialize(ValidCountry annotation) {
        validCountryCodes = Arrays.stream(CountryCode.values())
                .map(CountryCode::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.trim().isEmpty()) {
            return false;
        }

        return validCountryCodes.contains(value);
    }
}