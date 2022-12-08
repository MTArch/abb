package in.gov.abdm.abha.enrollment.validators.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.TimestampOtpValidator;

/**
 * Annotated interface for timestamp validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = TimestampOtpValidator.class)
@Target({ TYPE, FIELD })
public @interface TimestampOtp {

	String message() default AbhaConstants.VALIDATION_ERROR_TIMESTAMP_FIELD;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean optional() default false;
}
