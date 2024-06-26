package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.validators.annotations.ConsentCode;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validating consent code should not be null or empty
 *
 * It should match 'abha-enrollment'
 */
public class ConsentCodeValidator implements ConstraintValidator<ConsentCode, String> {
    private String consentCode = "abha-enrollment";
    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
                return (str!=null && str.equals(consentCode));
    }
}
