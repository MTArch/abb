package in.gov.abdm.abha.enrollment.validators;

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
     */
    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
        List<LoginHint> enumNames = Stream.of(LoginHint.values())
                .filter(name -> !name.equals(LoginHint.WRONG))
                .collect(Collectors.toList());
        if (  mobileOrEmailOtpRequestDto.getScope() != null && mobileOrEmailOtpRequestDto.getLoginHint()!= null) {
            boolean validLoginHint = enumNames.contains(mobileOrEmailOtpRequestDto.getLoginHint());
            List<Scopes> scopesList = mobileOrEmailOtpRequestDto.getScope().stream().distinct().collect(Collectors.toList());

        return isValidMobileOrEmailOtpRequestDto(mobileOrEmailOtpRequestDto,scopesList,validLoginHint);

        }else if(mobileOrEmailOtpRequestDto.getLoginHint()==null || !enumNames.contains(mobileOrEmailOtpRequestDto.getLoginHint()))
            return false;
        else
            return true;

    }

    private boolean isValidMobileOrEmailOtpRequestDto(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto,List<Scopes> scopesList,boolean validLoginHint){
        if (Common.isScopeAvailable(scopesList, Scopes.MOBILE_VERIFY)
                && (mobileOrEmailOtpRequestDto.getLoginHint() == null || !mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.MOBILE))) {
            validLoginHint = false;
        }
        if (Common.isAllScopesAvailable(scopesList, List.of(Scopes.ABHA_ENROL, Scopes.EMAIL_VERIFY))
                && !mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.EMAIL)) {
            validLoginHint = false;
        }
        if (scopesList.size() == 1 && Common.isScopeAvailable(scopesList, Scopes.ABHA_ENROL)
                && (mobileOrEmailOtpRequestDto.getLoginHint() == null || !mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.AADHAAR))) {
            validLoginHint = false;
        }
        if (Common.isAllScopesAvailable(scopesList, List.of(Scopes.ABHA_ENROL, Scopes.VERIFY_ENROLLMENT))
                && (mobileOrEmailOtpRequestDto.getLoginHint() == null || !mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.ENROLLMENT))) {
            validLoginHint = false;
        }
        return validLoginHint;
    }
}
