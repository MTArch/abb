package in.gov.abdm.abha.enrollment.validators.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.ChildAuthOtpValidator;
import in.gov.abdm.abha.enrollment.validators.OtpValidator;

/**
 * Annotated interface for otp validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = { OtpValidator.class, ChildAuthOtpValidator.class })
@Target({ TYPE })
public @interface Otp {
	String message() default AbhaConstants.VALIDATION_ERROR_OTP_OBJECT;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean encrypted() default false;

	boolean required() default true;
}
