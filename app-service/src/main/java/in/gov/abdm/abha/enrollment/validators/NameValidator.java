package in.gov.abdm.abha.enrollment.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.Name;

/**
 * Validating name should be a valid name
 *
 */
@SuppressWarnings("java:S6353")
public class NameValidator implements ConstraintValidator<Name, String> {
	
	private static final String NAME_REGEX_PATTERN = "^[A-Za-z\\s\\.\\']{1,}";

	@Override
	public boolean isValid(String name, ConstraintValidatorContext context) {
		if(name!=null && !name.isEmpty())
			return Pattern.compile(NAME_REGEX_PATTERN).matcher(name).matches();
		else
			return true;
	}
}
