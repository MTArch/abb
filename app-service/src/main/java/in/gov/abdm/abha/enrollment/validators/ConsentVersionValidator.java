package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.validators.annotations.ConsentVersion;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * Validating consent version should not be null or empty
 *
 * It should match '1.4'
 */
public class ConsentVersionValidator implements ConstraintValidator<ConsentVersion, String> {

    private String consentVersion = "1.4";
    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        return str!=null && str.equals(consentVersion);
    }
}
