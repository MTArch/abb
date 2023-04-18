package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.ValidDLNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DLNumberValidator implements ConstraintValidator<ValidDLNumber, String> {

    @Override
    public boolean isValid(String dlNumber, ConstraintValidatorContext context) {
        return dlNumber.matches("^(([A-Z]{2}[0-9]{2})( )|([A-Z]{2}-[0-9]{2}))((19|20)[0-9][0-9])[0-9]{7}$");
    }
}
