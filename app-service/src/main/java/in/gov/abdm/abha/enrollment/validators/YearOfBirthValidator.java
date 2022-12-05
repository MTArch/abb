package in.gov.abdm.abha.enrollment.validators;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import in.gov.abdm.abha.enrollment.validators.annotations.YOB;

/**
 * Validates year of birth
 *
 * it should be valid 4-digit number , cannot be greater than current year
 */
public class YearOfBirthValidator implements ConstraintValidator<YOB, String> {

	private static final String YOB_REGEX_PATTERN = "^(\\d{4})$";

	@Override
	public boolean isValid(String yob, ConstraintValidatorContext context) {
		try {
			if (yob != null && !yob.isEmpty()){
				return !yob.equals("0000") && Pattern.compile(YOB_REGEX_PATTERN).matcher(yob).matches()
						&& Integer.valueOf(yob) < LocalDateTime.now().getYear();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}
}
