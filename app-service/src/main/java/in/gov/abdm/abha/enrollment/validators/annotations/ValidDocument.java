package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.ValidDocumentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = ValidDocumentValidator.class)
@Target({FIELD})
public @interface ValidDocument {

    public String message() default AbhaConstants.VALIDATION_ERROR_DOCUMENT_FIELD;

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
    
    boolean optional() default false;
}
