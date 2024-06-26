package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.ConsentCodeValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated interface for consent code validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = ConsentCodeValidator.class)
@Target(FIELD)
public @interface ConsentCode {
    String message() default AbhaConstants.VALIDATION_ERROR_CONSENT_CODE_FIELD;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean optional() default false;
}
