package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.AbhaId;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
/**
 * Validating abha address should be valid
 */
public class AbhaValidator implements ConstraintValidator<AbhaId, String> {
    String pattern = "(^[a-zA-Z0-9]+[.]?[a-zA-Z0-9]*[_]?[a-zA-Z0-9]+$)|(^[a-zA-Z0-9]+[_]?[a-zA-Z0-9]*[.]?[a-zA-Z0-9]+$)";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value!=null && !value.isEmpty()) {
            if (StringUtils.hasLength(value) && value.contains("@")) {
                value = value.split("@")[0];
            }
            if(value.length()>=8 && value.length()<=18)
            {
                return Pattern.compile(pattern).matcher(value).matches() && validateNumericInput(value);
            }
        }
        return false;
    }

    private boolean validateNumericInput(String value) {
        return !(Pattern.compile("[0-9]+").matcher(value).matches() && value.length()==14);
    }
}
