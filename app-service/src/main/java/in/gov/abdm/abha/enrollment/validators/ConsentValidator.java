package in.gov.abdm.abha.enrollment.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.Consent;

/**
 * Validating consent should not be null or empty
 *
 */
public class ConsentValidator implements ConstraintValidator<Consent, EnrolByAadhaarRequestDto> {
	@Override
	public boolean isValid(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, ConstraintValidatorContext context) {
		return enrolByAadhaarRequestDto.getConsent() != null;
	}
}
