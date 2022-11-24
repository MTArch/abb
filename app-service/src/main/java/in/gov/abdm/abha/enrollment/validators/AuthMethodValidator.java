package in.gov.abdm.abha.enrollment.validators;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;

/**
 * Validating authmethods enum list should not be null or empty
 */
public class AuthMethodValidator implements ConstraintValidator<AuthMethod, AuthData> {
	
	@Override
    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {
    	 List<AuthMethods> requestScopes = authData.getAuthMethods();
    	 List<AuthMethods> enumNames =Stream.of(AuthMethods.values())
                 .filter(name -> {
                     return !name.equals(AuthMethods.WRONG);
                 })
                 .collect(Collectors.toList());
         return requestScopes != null &&  !requestScopes.isEmpty() && new HashSet<>(enumNames).containsAll(requestScopes);
    }
}
