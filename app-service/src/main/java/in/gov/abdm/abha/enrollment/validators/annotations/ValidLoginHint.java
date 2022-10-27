package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.LoginHintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * annotation interface to validate Login Hint
 */
@Retention(RUNTIME)
@Constraint(validatedBy = LoginHintValidator.class)
@Target({TYPE})
public @interface ValidLoginHint {

    public String message() default AbhaConstants.VALIDATION_ERROR_LOGIN_HINT_FIELD;
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};

}
