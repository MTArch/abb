package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.PreferredValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = { PreferredValidator.class})
@Target({FIELD})
public @interface Preferred {

    String message() default AbhaConstants.VALIDATION_ERROR_PREFERRED_FLAG;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
