package in.gov.abdm.abha.enrollment.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.Gender;
/**
 * Validating gender should match one of the values from enum
 *
 */
public class GenderValidator implements ConstraintValidator<Gender, String> {
    
    @Override
	public boolean isValid(String gender, ConstraintValidatorContext context) {
		if(gender != null && !gender.isEmpty())
			return in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.Gender.isValidByCode(gender);
		else
			return true;
	}
}
