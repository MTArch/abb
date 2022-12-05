package in.gov.abdm.abha.enrollment.validators;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidOtpSystem;

/**
 * Validates otp system should match the pre-defined values
 */
public class OtpSystemValidator implements ConstraintValidator<ValidOtpSystem, MobileOrEmailOtpRequestDto> {

	/**
	 * Implements the validation for otp system Possible values can be aadhaar and
	 * abdm
	 *
	 * @param otpSystem object to validate
	 * @param context   context in which the constraint is evaluated
	 *
	 * @return
	 */
	@Override
	public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
		List<OtpSystem> enumNames = Stream.of(OtpSystem.values()).filter(name -> {
			return !name.equals(OtpSystem.WRONG);
		}).collect(Collectors.toList());
		
		boolean validOtpSystem =  new HashSet<>(enumNames).contains(mobileOrEmailOtpRequestDto.getOtpSystem());

		if (Common.isScopeAvailable(mobileOrEmailOtpRequestDto.getScope(), Scopes.MOBILE_VERIFY)
				&& !mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM)) {
			validOtpSystem = false;
		}
		return validOtpSystem;
	}
}
