package in.gov.abdm.abha.enrollment.validators.annotations;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.UuidValidator;
/**
 * Annotated interface for uuid validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = UuidValidator.class)
@Target({FIELD})
public @interface Uuid {
    String message() default AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
