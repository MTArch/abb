package in.gov.abdm.abha.enrollment.validators.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.ScopeValidator;

/**
 * annotation interface to validate Scope attribute
 */
@Retention(RUNTIME)
@Constraint(validatedBy = ScopeValidator.class)
@Target({FIELD})
public @interface ValidScope {
    public String message() default AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD;
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
}
