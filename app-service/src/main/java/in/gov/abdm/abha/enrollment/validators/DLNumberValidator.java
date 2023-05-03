package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.ValidDLNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DLNumberValidator implements ConstraintValidator<ValidDLNumber, String> {

    @Override
    public boolean isValid(String dlNumber, ConstraintValidatorContext context) {
        return dlNumber.matches("^[a-zA-Z0-9]+([-\\s]{0,1})[a-zA-Z0-9]+$");
    }
}
