package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.ParentAbhaRequestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = ParentAbhaRequestValidator.class)
@Target({TYPE})
public @interface ValidParentAbhaRequest {

    public String message() default AbhaConstants.VALIDATION_ERROR_PARENT_ABHA_REQUEST_DEMO_OBJECT;

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
