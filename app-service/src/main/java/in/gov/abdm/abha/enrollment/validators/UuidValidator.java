package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UuidValidator implements ConstraintValidator<Uuid, String> {

    String UUID_REGEX_PATTERN = "[0-9abcdef-]{36}";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !StringUtils.isEmpty(value) && value!=null && Pattern.compile(UUID_REGEX_PATTERN).matcher(value).matches();
    }
}
