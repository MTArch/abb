package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.validators.annotations.Preferred;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PreferredValidator implements ConstraintValidator<Preferred, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value!=null && value.equals("1");
    }

}
