package in.gov.abdm.abha.enrollment.validators.annotations;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.OtpSystemValidator;

/**
 * annotation interface to validate Otp System
 */
@Retention(RUNTIME)
@Constraint(validatedBy = OtpSystemValidator.class)
@Target({TYPE})
public @interface ValidOtpSystem {

    public String message() default AbhaConstants.VALIDATION_ERROR_OTP_SYSTEM_FIELD;
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
}
