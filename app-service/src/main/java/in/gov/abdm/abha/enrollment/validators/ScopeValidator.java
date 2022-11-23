package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<Scopes> requestScopes = mobileOrEmailOtpRequestDto.getScope();
        List<Scopes> enumNames = Stream.of(Scopes.values())
                .filter(name -> {
                    return !name.equals(Scopes.WRONG);
                })
                .collect(Collectors.toList());
        return Common.isAllScopesAvailable(enumNames, requestScopes);
    }
}
