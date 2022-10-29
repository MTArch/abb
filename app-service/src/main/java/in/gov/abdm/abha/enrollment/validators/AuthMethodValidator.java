package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * Validating authmethods enum list should not be null or empty
 */
public class AuthMethodValidator implements ConstraintValidator<AuthMethod, AuthData>{
    @Override
    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {
        if (authData.getAuthMethods()!=null
                && !authData.getAuthMethods().isEmpty()
                   && authData.getAuthMethods().get(0) != null
                      && !authData.getAuthMethods().get(0).equals("")
                            && !authData.getAuthMethods().stream().anyMatch(v->v.equals(AuthMethods.EMPTY)))
            return true;
        else
            return false;
    }
}
