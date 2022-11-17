package in.gov.abdm.abha.enrollment.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidLoginHint;

/**
 * Validating login hint should be empty for abha creation using aadhaar
 */
public class LoginHintValidator implements ConstraintValidator<ValidLoginHint, MobileOrEmailOtpRequestDto> {

	/**
	 * Implements the validation for loginHint LoginHint should be empty for abha
	 * creation using aadhaar flow
	 *
	 * @param mobileOrEmailOtpRequestDto object to validate
	 * @param context                    context in which the constraint is
	 *                                   evaluated
	 * @return
	 */
	@Override
	public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
		if (mobileOrEmailOtpRequestDto != null)
			return (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.AADHAAR)
					|| mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.ABHA_NUMBER)
					|| mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.MOBILE)
					|| mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.EMPTY));
		else
			return false;

	}
}
