package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.OtpSystemValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * annotation interface to validate Otp System
 */
@Retention(RUNTIME)
@Constraint(validatedBy = OtpSystemValidator.class)
@Target({FIELD})
public @interface ValidOtpSystem {

    public String message() default AbhaConstants.VALIDATION_ERROR_OTP_SYSTEM_FIELD;
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
}
