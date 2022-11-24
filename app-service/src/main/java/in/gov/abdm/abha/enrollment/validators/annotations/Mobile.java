package in.gov.abdm.abha.enrollment.validators.annotations;
import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.MOBILE_NUMBER_MISSMATCH;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.validators.MobileFieldValidator;
import in.gov.abdm.abha.enrollment.validators.MobileValidator;

/**
 * Annotated interface for mobile number validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = {MobileValidator.class, MobileFieldValidator.class})
@Target({TYPE, FIELD})
public @interface Mobile {

    String message() default MOBILE_NUMBER_MISSMATCH;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean optional() default false;

    boolean encrypted() default false;
}
