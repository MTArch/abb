package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.TimestampOtpValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * Annotated interface for timestamp validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = TimestampOtpValidator.class)
@Target({TYPE})
public @interface TimestampOtp {

    String message() default AbhaConstants.VALIDATION_ERROR_TIMESTAMP_FIELD;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean optional() default false;
}
