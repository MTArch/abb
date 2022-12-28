package in.gov.abdm.abha.enrollment.validators;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
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
        if(mobileOrEmailOtpRequestDto.getScope() == null){
            return false;
        }
        List<LoginHint> enumNames = Stream.of(LoginHint.values())
                .filter(name -> {
                    return !name.equals(LoginHint.WRONG);
                })
                .collect(Collectors.toList());

        boolean validLoginHint = new HashSet<>(enumNames).contains(mobileOrEmailOtpRequestDto.getLoginHint());

        if (Common.isScopeAvailable(mobileOrEmailOtpRequestDto.getScope().stream().distinct().collect(Collectors.toList()), Scopes.MOBILE_VERIFY)
                && !mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.MOBILE)) {
			validLoginHint = false;
        }
		return validLoginHint;
    }
}
