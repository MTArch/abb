package in.gov.abdm.abha.enrollment.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.Mobile;

/**
 * Validating mobile number should be encrypted and valid
 *
 */
public class MobileFieldValidator implements ConstraintValidator<Mobile, String> {

	private static final String MOBILE_NUMBER_REGEX_PATTERN = "(\\+91|0)?[1-9][0-9]{9}";

	@Override
	public boolean isValid(String mobile, ConstraintValidatorContext constraintValidatorContext) {
		return mobile != null && !mobile.isEmpty()
				&& Pattern.compile(MOBILE_NUMBER_REGEX_PATTERN).matcher(mobile).matches();
	}

}
