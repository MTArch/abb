package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidMobileOrEmailOtpRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileOrEmailOtpRequestValidator implements ConstraintValidator<ValidMobileOrEmailOtpRequest, MobileOrEmailOtpRequestDto> {

    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
        return isValidTxnId(mobileOrEmailOtpRequestDto)
                && isValidScope(mobileOrEmailOtpRequestDto)
                && isValidLoginHint(mobileOrEmailOtpRequestDto)
                && isValidLoginId(mobileOrEmailOtpRequestDto);
    }

    private boolean isValidLoginId(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        return false;
    }

    private boolean isValidLoginHint(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        return false;
    }
    private boolean isValidScope(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        return false;
    }

    private boolean isValidTxnId(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {

        return false;
    }
}
