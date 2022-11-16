package in.gov.abdm.abha.enrollment.validators.annotations;


import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.RelationshipValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = RelationshipValidator.class)
@Target({TYPE})
public @interface ValidRelationship {

    public String message() default AbhaConstants.VALIDATION_ERROR_RELATIONSHIP_FIELD;

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
