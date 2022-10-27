package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.validators.NameValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.INVALID_NAME_FORMAT;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated interface for name validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = NameValidator.class)
@Target({TYPE})
public @interface Name {
    String message() default INVALID_NAME_FORMAT;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
