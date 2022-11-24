package in.gov.abdm.abha.enrollment.validators.annotations;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.TransactionIdValidator;

/**
 * annotation interface to validate Transaction Id
 */
@Retention(RUNTIME)
@Constraint(validatedBy = TransactionIdValidator.class)
@Target({TYPE})
public @interface ValidTransactionId {

    public String message() default AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD;
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
}
