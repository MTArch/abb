package in.gov.abdm.abha.enrollment.validators.request;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class HelperUtil {
    public boolean isScopeAvailable(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, List<Scopes> scope){
        return mobileOrEmailOtpRequestDto.getScope()!=null && mobileOrEmailOtpRequestDto.getScope().stream().distinct().anyMatch(scope::contains);
    }
}
