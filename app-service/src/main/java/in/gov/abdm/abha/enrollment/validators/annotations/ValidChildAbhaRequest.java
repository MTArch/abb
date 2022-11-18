package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.ChildAbhaRequestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = ChildAbhaRequestValidator.class)
@Target({TYPE})
public @interface ValidChildAbhaRequest {

    public String message() default AbhaConstants.VALIDATION_ERROR_CHILD_ABHA_REQUEST_DEMO_OBJECT;

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
