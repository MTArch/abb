package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.YearOfBirthValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * Annotated interface for yob validation
 */
@Retention(RUNTIME)
@Constraint(validatedBy = YearOfBirthValidator.class)
@Target({FIELD})
public @interface YOB {
    String message() default AbhaConstants.PATTERN_MISMATCHED;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    boolean optional() default false;

}
