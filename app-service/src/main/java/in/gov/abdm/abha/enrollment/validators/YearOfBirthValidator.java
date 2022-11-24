package in.gov.abdm.abha.enrollment.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.YOB;

/**
 * Validates year of birth
 *
 * it should be valid 4-digit number
 */
public class YearOfBirthValidator implements ConstraintValidator<YOB, String> {

	private static final String YOB_REGEX_PATTERN = "^(\\d{4})$";

	@Override
	public boolean isValid(String yob, ConstraintValidatorContext context) {
		return yob != null && !yob.isEmpty() && Pattern.compile(YOB_REGEX_PATTERN).matcher(yob).matches();
	}

}
