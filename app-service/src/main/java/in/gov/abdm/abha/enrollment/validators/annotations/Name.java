package in.gov.abdm.abha.enrollment.validators.annotations;
import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.INVALID_NAME_FORMAT;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.validators.NameValidator;

/**
 * Annotated interface for name validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = NameValidator.class)
@Target({TYPE,FIELD})
public @interface Name {
    String message() default INVALID_NAME_FORMAT;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    boolean optional() default false;
}
