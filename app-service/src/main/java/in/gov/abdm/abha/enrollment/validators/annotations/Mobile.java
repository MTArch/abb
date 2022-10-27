package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.validators.MobileValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.MOBILE_NUMBER_MISSMATCH;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated interface for mobile number validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = MobileValidator.class)
@Target({TYPE})
public @interface Mobile {

    String message() default MOBILE_NUMBER_MISSMATCH;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean optional() default false;

    boolean encrypted() default false;
}
