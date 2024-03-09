package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;

@ExtendWith(SpringExtension.class)
public class ConsentValidatorTests {
    @InjectMocks
    ConsentValidator consentValidator;
    @InjectMocks
    ConsentCodeValidator consentCodeValidator;
    @InjectMocks
    ConsentVersionValidator consentVersionValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Test
    public void isValidtest(){
        Boolean res = consentCodeValidator.isValid("abha-enrollment",constraintValidatorContext);
    }
    @Test
    public void isValidtest2(){
        Boolean res = consentVersionValidator.isValid("1.4",constraintValidatorContext);
    }
    @Test
    public void isValidtest3(){
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto=new EnrolByAadhaarRequestDto();
        enrolByAadhaarRequestDto.setConsent(new ConsentDto());

        Boolean res = consentValidator.isValid(enrolByAadhaarRequestDto,constraintValidatorContext);
    }
}
