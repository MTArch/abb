package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.AbhaId;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class AbhaValidator implements ConstraintValidator<AbhaId, String> {
    String parttern = "^[A-Za-z](([A-Za-z0-9]{3,31})|(([A-Za-z0-9]*\\.[A-Za-z0-9]+)))$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(value) || Pattern.compile(parttern).matcher(value).matches();
    }
}
