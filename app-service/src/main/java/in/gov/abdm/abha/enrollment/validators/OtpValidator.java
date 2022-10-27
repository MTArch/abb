package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.Otp;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OtpValidator implements ConstraintValidator<Otp, AuthData> {
    @Override
    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {
        if (!authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null
                && !authData.getAuthMethods().contains(AuthMethods.WRONG))
        {
            return !StringUtils.isEmpty(authData.getOtp())
                    && authData.getOtp()!=null
                    && authData.getOtp().getTimeStamp()!=null
                    && !authData.getOtp().getTimeStamp().isEmpty()
                    && authData.getOtp().getTxnId()!=null
                    && !authData.getOtp().getTxnId().isEmpty()
                    && authData.getOtp().getOtpValue()!=null
                    && !authData.getOtp().getOtpValue().isEmpty();
        } else if (authData.getAuthMethods().stream().noneMatch(v->v.equals(AuthMethods.OTP))
                || authData.getAuthMethods().isEmpty()
                || authData.getAuthMethods()==null)
        {
            return true;
        }
        return false;
    }
}
