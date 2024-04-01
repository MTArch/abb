package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.AadhaarNumber;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;

@ExtendWith(SpringExtension.class)
public class AadhaarNumberValidatortests {
    @InjectMocks
    AadhaarNumberValidator aadhaarNumberValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    @Test
    public void isVlidtest() {

         Boolean res = aadhaarNumberValidator.isValid("123456789087",constraintValidatorContext);
        Assert.assertEquals(true,res);
    }
}
