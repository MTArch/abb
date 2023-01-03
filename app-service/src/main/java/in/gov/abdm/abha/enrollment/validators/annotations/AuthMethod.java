package in.gov.abdm.abha.enrollment.validators.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.AuthMethodValidator;
import in.gov.abdm.abha.enrollment.validators.ChildAuthMethodsValidator;


/**
 * Annotated interface for authMethod validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = { AuthMethodValidator.class, ChildAuthMethodsValidator.class })
@Target({ TYPE })
public @interface AuthMethod {
	String message() default AbhaConstants.VALIDATION_EMPTY_AUTH_METHOD;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean optional() default false;
}
