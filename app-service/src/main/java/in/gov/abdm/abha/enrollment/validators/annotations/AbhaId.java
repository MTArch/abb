package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.AbhaValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated interface for abha number validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = AbhaValidator.class)
@Target({TYPE, FIELD, PARAMETER})
public @interface AbhaId {
    String message() default AbhaConstants.VALIDATION_ERROR_ABHA_ADDRESS_FIELD;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
