package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.UuidValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * Annotated interface for uuid validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = UuidValidator.class)
@Target({ TYPE})
public @interface Uuid {
    String message() default AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
