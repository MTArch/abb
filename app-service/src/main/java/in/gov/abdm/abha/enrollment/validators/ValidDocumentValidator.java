package in.gov.abdm.abha.enrollment.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.ValidDocument;

public class ValidDocumentValidator implements ConstraintValidator<ValidDocument, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value != null && !value.isEmpty();
	}
}
