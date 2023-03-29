package in.gov.abdm.abha.enrollment.validators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;

/**
 * Validates scope attribute from the provided list of scopes against the pre-decided value ie. abha-enrol
 */
public class ScopeValidator implements ConstraintValidator<ValidScope, List<Scopes>> {

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
    public boolean isValid(List<Scopes> requestScopes, ConstraintValidatorContext context) {
        if(requestScopes==null)
            return false;
        List<Scopes> enumNames = Stream.of(Scopes.values())
                .filter(name -> {
                    return !name.equals(Scopes.WRONG);
                })
                .collect(Collectors.toList());
        return requestScopes != null &&  !requestScopes.isEmpty() && Common.isAllScopesAvailable(enumNames, requestScopes);
    }
}
