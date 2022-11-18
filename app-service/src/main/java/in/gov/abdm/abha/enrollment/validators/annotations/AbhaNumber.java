package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.validators.AbhaNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.VALIDATION_NULL_ABHA_NUMBER;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = AbhaNumberValidator.class)
@Target({TYPE, FIELD, PARAMETER})
public @interface AbhaNumber {

    String message() default VALIDATION_NULL_ABHA_NUMBER;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
