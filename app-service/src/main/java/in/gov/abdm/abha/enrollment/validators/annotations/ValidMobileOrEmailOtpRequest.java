package in.gov.abdm.abha.enrollment.validators.annotations;
import in.gov.abdm.abha.enrollment.validators.MobileOrEmailOtpRequestValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = MobileOrEmailOtpRequestValidator.class)
@Target({TYPE})
public @interface ValidMobileOrEmailOtpRequest {

    public String message() default "Invalid Request Field";
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
}
