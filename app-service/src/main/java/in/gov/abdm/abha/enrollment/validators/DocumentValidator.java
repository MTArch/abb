package in.gov.abdm.abha.enrollment.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.ValidDocument;

public class DocumentValidator implements ConstraintValidator<ValidDocument, String> {
	
	@Override
	public boolean isValid(String document, ConstraintValidatorContext context) {
		return document != null && !document.isEmpty();
	}
}
