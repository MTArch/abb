package in.gov.abdm.abha.enrollment.validators.annotations;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.OtpValueValidator;
/**
 * Annotated interface for otp value validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = OtpValueValidator.class)
@Target({ FIELD})
public @interface OtpValue {

        String message() default AbhaConstants.VALIDATION_ERROR_OTP_VALUE_FIELD;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        boolean encrypted() default false;

        boolean required() default true;
}
