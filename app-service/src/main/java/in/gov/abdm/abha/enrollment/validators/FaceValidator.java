package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.Face;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validating bio object :
 *
 * if authMethods is 'otp' then bio can be null ,
 * if authMethods is 'pi' then bio object should not be null
 */
public class FaceValidator implements ConstraintValidator<Face, AuthData> {
    @Override
    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {
        if (authData.getAuthMethods() != null && !authData.getAuthMethods().isEmpty() && authData.getAuthMethods().get(0) != null && authData.getAuthMethods().contains(AuthMethods.FACE))
        {
            if(bioNullorEmpty(authData))
                return false;
            else if(bioNotNullorEmpty(authData))
                return true;
        }
        else if (authData.getAuthMethods() != null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null
                && authData.getAuthMethods().stream().noneMatch(v -> v.equals(AuthMethods.FACE)))
        {
            return (bioNullorEmpty(authData) || bioNotNullorEmpty(authData));
        }
        else
            return true;
        return false;
    }

    private boolean bioNullorEmpty(AuthData authData) {
        return (ObjectUtils.isEmpty(authData.getFace())
                || authData.getFace() == null)
                || ((authData.getFace().getAadhaar()==null
                || authData.getFace().getAadhaar().isEmpty())
                || (authData.getFace().getRdPidData()==null
                || authData.getFace().getRdPidData().isEmpty()));
    }

    private boolean bioNotNullorEmpty(AuthData authData) {
        return !ObjectUtils.isEmpty(authData.getFace())
                && authData.getFace()!=null
                && authData.getFace().getAadhaar()!=null
                && !authData.getFace().getAadhaar().isEmpty()
                && authData.getFace().getRdPidData()!=null
                && !authData.getFace().getRdPidData().isEmpty();
    }
}
