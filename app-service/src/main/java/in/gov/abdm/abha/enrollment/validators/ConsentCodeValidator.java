package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.ConsentCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConsentCodeValidator implements ConstraintValidator<ConsentCode, String> {
    private String CONSENT_CODE = "abha-enrollment";
    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
                return (str!=null && str.equals(CONSENT_CODE));
    }
}
