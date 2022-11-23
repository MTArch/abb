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

    String UUID_REGEX_PATTERN = "[0-9abcdef-]{36}";

    @Override
	public boolean isValid(String uuid, ConstraintValidatorContext context) {
		return uuid != null && !uuid.isEmpty() && Pattern.compile(UUID_REGEX_PATTERN).matcher(uuid).matches();
	}

    
}
