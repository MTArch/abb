package in.gov.abdm.abha.enrollment.validators.annotations;

import in.gov.abdm.abha.enrollment.validators.YearOfBrithValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.PATTREN_MISMATCHED;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = YearOfBrithValidator.class)
@Target({ FIELD, PARAMETER })
public @interface YOB {
    String message() default PATTREN_MISMATCHED;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
