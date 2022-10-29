package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidLoginHint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validating login hint should be empty for abha creation using aadhaar
 */
public class LoginHintValidator implements ConstraintValidator<ValidLoginHint, MobileOrEmailOtpRequestDto> {

    /**
     * Implements the validation for loginHint
     * LoginHint should be empty for abha creation using aadhaar flow
     *
     * @param mobileOrEmailOtpRequestDto object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
        return mobileOrEmailOtpRequestDto.getLoginHint() != null && mobileOrEmailOtpRequestDto.getLoginHint().equals(StringConstants.EMPTY);
    }
}
