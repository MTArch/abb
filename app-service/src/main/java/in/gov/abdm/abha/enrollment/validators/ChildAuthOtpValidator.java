package in.gov.abdm.abha.enrollment.validators;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.ObjectUtils;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.Otp;
/**
 * Validating otp object :
 *
 * if authMethods is 'otp', then otp object can not be null,
 * if authMethods is 'pi' then otp object can be null
 */
public class ChildAuthOtpValidator implements ConstraintValidator<Otp, AuthData> {
    @Override
    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {

        if (authData.getAuthMethods()!=null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null && !authData.getAuthMethods().get(0).equals("")
                && authData.getAuthMethods().contains(AuthMethods.OTP))
        {
            if(otpNullorEmpty(authData))
                return false;
            else if(otpNotNullorEmpty(authData))
                return true;
        }
        else if (authData.getAuthMethods()!=null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null && !authData.getAuthMethods().get(0).equals("")
                && authData.getAuthMethods().stream().noneMatch(v->v.equals(AuthMethods.OTP)))
        {
            if(otpNullorEmpty(authData))
                return true;
            else if(otpNotNullorEmpty(authData))
                return true;
        }
       else
           return true;
        return false;
    }

    private boolean otpNotNullorEmpty(AuthData authData) {
        return !ObjectUtils.isEmpty(authData.getOtp())
                && authData.getOtp()!=null
                && authData.getOtp().getTimeStamp()!=null
                && !authData.getOtp().getTimeStamp().isEmpty()
                && authData.getOtp().getTxnId()!=null
                && !authData.getOtp().getTxnId().isEmpty()
                && authData.getOtp().getOtpValue()!=null
                && !authData.getOtp().getOtpValue().isEmpty();
    }

    private boolean otpNullorEmpty(AuthData authData) {
        return ObjectUtils.isEmpty(authData.getOtp())
                || authData.getOtp() == null
                || authData.getOtp().getTimeStamp() ==null
                || authData.getOtp().getTimeStamp().isEmpty()
                || authData.getOtp().getTxnId()==null
                || authData.getOtp().getTxnId().isEmpty()
                || authData.getOtp().getOtpValue()==null
                || authData.getOtp().getOtpValue().isEmpty();
    }
}
