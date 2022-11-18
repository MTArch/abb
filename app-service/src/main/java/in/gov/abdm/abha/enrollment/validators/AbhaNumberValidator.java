package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.AbhaNumber;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class AbhaNumberValidator implements ConstraintValidator<AbhaNumber, String> {

    private String ABHA_NUMBER_PATTERN = "(\\d{2})(\\d{4})(\\d{4})(\\d{4})";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(value) || Pattern.compile(ABHA_NUMBER_PATTERN).matcher(value).matches();
    }
}
