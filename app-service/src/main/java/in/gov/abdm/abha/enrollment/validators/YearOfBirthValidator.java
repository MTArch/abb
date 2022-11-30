package in.gov.abdm.abha.enrollment.validators;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private String DATE_TIME_FORMATTER = "yyyy";
	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

	@Override
	public boolean isValid(String yob, ConstraintValidatorContext context) {
		try {
			if( yob != null && !yob.isEmpty())
				return true;
			
			int currentYear = LocalDateTime.now().getYear();

			return yob != null && !yob.isEmpty() && Integer.parseInt(yob) <= currentYear
					&& Pattern.compile(YOB_REGEX_PATTERN).matcher(yob).matches();
		} catch (Exception ex) {
			return false;
		}

	}

}
