package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.FaceValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated interface for bio validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = FaceValidator.class)
@Target({TYPE})
public @interface Face {
    String message() default AbhaConstants.VALIDATION_ERROR_BIO_OBJECT;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean encrypted() default false;
    boolean required() default true;
}
