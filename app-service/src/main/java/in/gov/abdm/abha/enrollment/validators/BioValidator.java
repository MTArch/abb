package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.Bio;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validating bio object :
 *
 * if authMethods is 'otp' then bio can be null ,
 * if authMethods is 'pi' then bio object should not be null
 */
public class BioValidator implements ConstraintValidator<Bio, AuthData> {
    @Override
    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {
        if (authData.getAuthMethods()!=null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null && !authData.getAuthMethods().get(0).equals("")
                && authData.getAuthMethods().contains(AuthMethods.PI))
        {
            if(bioNullorEmpty(authData))
                return false;
            else if(bioNotNullorEmpty(authData))
                return true;
        }
        else if (authData.getAuthMethods()!=null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null && !authData.getAuthMethods().get(0).equals("")
                && authData.getAuthMethods().stream().noneMatch(v->v.equals(AuthMethods.PI)))
        {
            return (bioNullorEmpty(authData) || bioNotNullorEmpty(authData));
        }
        else
            return true;
        return false;
    }

    private boolean bioNullorEmpty(AuthData authData) {
        return (StringUtils.isEmpty(authData.getBio())
                || authData.getBio() == null)
                || ((authData.getBio().getTimestamp() ==null
                || authData.getBio().getTimestamp().isEmpty())
                || (authData.getBio().getAadhaar()==null
                || authData.getBio().getAadhaar().isEmpty())
                || (authData.getBio().getRdPidData()==null
                || authData.getBio().getRdPidData().isEmpty()));
    }

    private boolean bioNotNullorEmpty(AuthData authData) {
        return !StringUtils.isEmpty(authData.getBio())
                && authData.getBio()!=null
                && authData.getBio().getTimestamp()!=null
                && !authData.getBio().getTimestamp().isEmpty()
                && authData.getBio().getAadhaar()!=null
                && !authData.getBio().getAadhaar().isEmpty()
                && authData.getBio().getRdPidData()!=null
                && !authData.getBio().getRdPidData().isEmpty();
    }
}
