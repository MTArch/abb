package in.gov.abdm.abha.enrollment.validators.annotations;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.GenderValidator;

/**
 * Annotated interface for gender validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
@Target({FIELD})
public @interface Gender {

    String message() default AbhaConstants.VALIDATION_ERROR_GENDER_FIELD;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean required() default false;
}
