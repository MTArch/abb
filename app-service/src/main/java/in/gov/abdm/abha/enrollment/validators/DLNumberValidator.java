package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.ValidDLNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DLNumberValidator implements ConstraintValidator<ValidDLNumber, String> {

    @Override
    public boolean isValid(String dlNumber, ConstraintValidatorContext context) {
        return dlNumber.matches("^(([A-Za-z]{2}[\\d]{2})( )|([A-Za-z]{2}-[\\d]{2}))((19|20)[\\d][\\d])[\\d]{1,7}$");
    }
}
