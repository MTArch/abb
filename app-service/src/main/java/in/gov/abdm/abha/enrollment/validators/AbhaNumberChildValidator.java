package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.validators.annotations.AbhaNumberChild;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class AbhaNumberChildValidator implements ConstraintValidator<AbhaNumberChild, String> {
    private String abhaNumberPattern = "\\d{2}-\\d{4}-\\d{4}-\\d{4}";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.isEmpty() && Pattern.compile(abhaNumberPattern).matcher(value).matches();
    }
}
