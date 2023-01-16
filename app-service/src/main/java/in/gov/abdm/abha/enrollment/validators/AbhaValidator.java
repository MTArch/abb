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
    String pattern = "^[A-Za-z](([A-Za-z0-9]{3,31})|(([A-Za-z0-9]*\\.[A-Za-z0-9]+)))$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value!=null && !value.isEmpty()) {
            if (StringUtils.hasLength(value) && value.contains("@")) {
                value = value.split("@")[0];
            }
            return Pattern.compile(pattern).matcher(value).matches();
        }
        return false;
    }
}
