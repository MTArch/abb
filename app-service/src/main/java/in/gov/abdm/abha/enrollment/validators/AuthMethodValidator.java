package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthMethodValidator implements ConstraintValidator<AuthMethod, AuthData>{
    @Override
    public boolean isValid(AuthData value, ConstraintValidatorContext context) {
        return false;
    }
}
