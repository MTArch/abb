package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.YOB;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class YearOfBrithValidator implements ConstraintValidator<YOB, String> {
    String parttern = "^(\\d{4})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return StringUtils.isEmpty(value)|| Pattern.compile(parttern).matcher(value).matches();
    }
}
