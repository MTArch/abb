package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.TimestampValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * Annotated interface for timestamp validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = TimestampValidator.class)
@Target({FIELD})
public @interface Timestamp {

    String message() default AbhaConstants.VALIDATION_ERROR_TIMESTAMP_FIELD;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean optional() default false;
}
