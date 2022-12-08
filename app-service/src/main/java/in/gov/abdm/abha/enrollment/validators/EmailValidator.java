package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.validators.annotations.Email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {

    public static final String EMAIL_REGEX_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if(email != null && !email.isEmpty())
            return Pattern.compile(EMAIL_REGEX_PATTERN).matcher(email).matches();
        else
            return true;
    }
}
