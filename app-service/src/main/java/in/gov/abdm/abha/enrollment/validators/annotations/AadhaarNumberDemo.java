package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.AadhaarNumberDemoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated interface for aadhaar number validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = AadhaarNumberDemoValidator.class)
@Target({TYPE})

public @interface AadhaarNumberDemo {
    String message() default AbhaConstants.AADHAAR_NUMBER_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean optional() default false;

    boolean encrypted() default false;

}
