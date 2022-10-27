package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.validators.annotations.ConsentVersion;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConsentVersionValidator implements ConstraintValidator<ConsentVersion, String> {
    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        return str!=null && str.equals("1.4");
    }
}
