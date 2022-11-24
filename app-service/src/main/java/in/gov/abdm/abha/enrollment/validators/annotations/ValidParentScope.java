package in.gov.abdm.abha.enrollment.validators.annotations;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.ParentScopeValidator;

@Retention(RUNTIME)
@Constraint(validatedBy = ParentScopeValidator.class)
@Target({TYPE})
public @interface ValidParentScope {

    public String message() default AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD;

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
