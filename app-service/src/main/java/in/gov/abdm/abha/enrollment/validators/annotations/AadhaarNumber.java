package in.gov.abdm.abha.enrollment.validators.annotations;
import javax.validation.Constraint;
import javax.validation.Payload;
import in.gov.abdm.abha.enrollment.validators.AadhaarNumberValidator;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.AADHAAR_NUMBER_INVALID;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated interface for aadhaar number validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = AadhaarNumberValidator.class)
@Target({FIELD, PARAMETER })

public @interface AadhaarNumber {
    String message() default AADHAAR_NUMBER_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean optional() default false;

    boolean encrypted() default false;

}
