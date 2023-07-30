package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.AbhaId;
import org.apache.commons.lang3.StringUtils;

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
        if (((StringUtils.isEmpty(value) && !value.isEmpty()) && !value.split("@")[0].equals("@abdm") || !value.split("@")[0].equals("@sbx")) &&
                (value.length() >= 8 && value.length() <= 18)) {
            return Pattern.compile(pattern).matcher(value).matches();
        }
        return false;
    }
}
