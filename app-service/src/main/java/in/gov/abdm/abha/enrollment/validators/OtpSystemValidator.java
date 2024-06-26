package in.gov.abdm.abha.enrollment.validators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
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
     * @return
     */
    @Override
    @SuppressWarnings("java:S3776")
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
        List<OtpSystem> enumNames = Stream.of(OtpSystem.values())
                .filter(name -> !name.equals(OtpSystem.WRONG))
                .collect(Collectors.toList());

        if (mobileOrEmailOtpRequestDto.getScope() != null && mobileOrEmailOtpRequestDto.getOtpSystem() != null && mobileOrEmailOtpRequestDto.getLoginHint() != null) {


            boolean validOtpSystem = enumNames.contains(mobileOrEmailOtpRequestDto.getOtpSystem());

            if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.AADHAAR)
                    && !mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.AADHAAR)) {
                validOtpSystem = false;
            }

            if (Common.isScopeAvailable(mobileOrEmailOtpRequestDto.getScope().stream().distinct().collect(Collectors.toList()), Scopes.MOBILE_VERIFY)
                    && !mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM)) {
                validOtpSystem = false;
            }
            if (Common.isAllScopesAvailable(mobileOrEmailOtpRequestDto.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.EMAIL_VERIFY))
                    && !mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM)) {
                validOtpSystem = false;
            }
            if (Common.isAllScopesAvailable(mobileOrEmailOtpRequestDto.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.VERIFY_ENROLLMENT))
                    && !mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM)) {
                validOtpSystem = false;
            }
            return validOtpSystem;
        } else {
            return !(mobileOrEmailOtpRequestDto.getOtpSystem() == null || !enumNames.contains(mobileOrEmailOtpRequestDto.getOtpSystem()));
        }
    }
}
