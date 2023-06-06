package in.gov.abdm.abha.enrollment.validators;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;

/**
 * Validates uuid/txn id
 *
 * it should match regex pattern
 */
public class UuidValidator implements ConstraintValidator<Uuid, String> {

    String uuidRegexPattern ="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    @Override
	public boolean isValid(String uuid, ConstraintValidatorContext context) {
		return uuid != null && !uuid.isEmpty() && Pattern.compile(uuidRegexPattern).matcher(uuid).matches();
	}

    
}
