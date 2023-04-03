package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.validators.annotations.Email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.EMAIL_REGEX_PATTERN;

public class EmailValidator implements ConstraintValidator<Email, String> {



    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if(email != null && !email.isEmpty())
            return Pattern.compile(EMAIL_REGEX_PATTERN).matcher(email).matches();
        else
            return true;
    }
}
