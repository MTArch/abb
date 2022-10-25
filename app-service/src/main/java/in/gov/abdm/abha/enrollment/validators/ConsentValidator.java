package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.Consent;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConsentValidator implements ConstraintValidator<Consent, EnrolByAadhaarRequestDto> {
    @Override
    public boolean isValid(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, ConstraintValidatorContext context) {

        if(enrolByAadhaarRequestDto.getConsent()!=null || !StringUtils.isEmpty(enrolByAadhaarRequestDto.getConsent()))
        {
            return true;
        }
        else
            return false;
    }
}
