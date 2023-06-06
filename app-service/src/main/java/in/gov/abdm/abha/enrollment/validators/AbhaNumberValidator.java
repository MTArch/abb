package in.gov.abdm.abha.enrollment.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.AbhaNumber;

public class AbhaNumberValidator implements ConstraintValidator<AbhaNumber, String> {

	private String abhaNumberPattern = "\\d{2}-\\d{4}-\\d{4}-\\d{4}";

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value != null && !value.isEmpty() && Pattern.compile(abhaNumberPattern).matcher(value).matches();
	}
}
