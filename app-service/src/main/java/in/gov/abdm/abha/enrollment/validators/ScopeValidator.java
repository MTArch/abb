package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.request.ScopeEnum;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates scope attribute from the provided list of scopes against the pre-decided value ie. abha-enrol
 */
public class ScopeValidator implements ConstraintValidator<ValidScope, MobileOrEmailOtpRequestDto> {

    /**
     * Implements the validation for scope field
     * Scope should be abha-enrol for the abha creation using aadhaar flow
     *
     * @param mobileOrEmailOtpRequestDto object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
        return !mobileOrEmailOtpRequestDto.getScope().isEmpty()
                && mobileOrEmailOtpRequestDto.getScope().get(0) != null
                && !mobileOrEmailOtpRequestDto.getScope().contains(ScopeEnum.WRONG);
    }
}
