package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.Otp;
import org.springframework.util.ObjectUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * Validating otp object :
 *
 * if authMethods is 'otp', then otp object can not be null,
 * if authMethods is 'pi' then otp object can be null
 */
public class OtpValidator implements ConstraintValidator<Otp, AuthData> {
    @Override
    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {

        if (authData.getAuthMethods()!=null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null
                && authData.getAuthMethods().contains(AuthMethods.OTP))
        {
            if(otpNullorEmpty(authData))
                return false;
            else if(otpNotNullorEmpty(authData))
                return true;
        }
        else if (authData.getAuthMethods()!=null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null
                && authData.getAuthMethods().stream().noneMatch(v->v.equals(AuthMethods.OTP)))
        {
            if(otpNullorEmpty(authData)|| otpNotNullorEmpty(authData)) {
                return true;
            }
        }
       else
           return true;
        return false;
    }

    private boolean otpNotNullorEmpty(AuthData authData) {
        return !ObjectUtils.isEmpty(authData.getOtp())
                && authData.getOtp()!=null
                && authData.getOtp().getTxnId()!=null
                && !authData.getOtp().getTxnId().isEmpty()
                && authData.getOtp().getOtpValue()!=null
                && !authData.getOtp().getOtpValue().isEmpty();
    }

    private boolean otpNullorEmpty(AuthData authData) {
        return ObjectUtils.isEmpty(authData.getOtp())
                || authData.getOtp() == null
                || authData.getOtp().getTxnId()==null
                || authData.getOtp().getTxnId().isEmpty()
                || authData.getOtp().getOtpValue()==null
                || authData.getOtp().getOtpValue().isEmpty();
    }
}
