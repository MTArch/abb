package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.AbhaId;

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
            if(!value.split("@")[0].equals("@abdm") || !value.split("@")[0].equals("@sbx")) {
                if (value.length() >= 8 && value.length() <= 18) {
                    return Pattern.compile(pattern).matcher(value).matches();
                }
            }
        }
        return false;
    }
}
