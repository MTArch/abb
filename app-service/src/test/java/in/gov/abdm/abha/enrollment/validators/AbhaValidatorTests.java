package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;

@ExtendWith(SpringExtension.class)
public class AbhaValidatorTests {
    @InjectMocks
    AbhaValidator abhaValidator;
    @InjectMocks
    AbhaNumberChildValidator abhaNumberChildValidator;
    @InjectMocks
    AbhaNumberValidator abhaNumberValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Test
    public void abhaNumberChildValidatorTest(){
        Boolean response = abhaNumberChildValidator.isValid(CommonTestData.ABHA_NUMBER_VALID,constraintValidatorContext);
        Assert.assertEquals(true,response);
    }
    @Test
    public void abhaNumberValidatorTest(){
        Boolean response = abhaNumberValidator.isValid(CommonTestData.ABHA_NUMBER_VALID,constraintValidatorContext);
        Assert.assertEquals(true,response);
    }
    @Test
    public void abhaValidatorTest(){
        Boolean response = abhaValidator.isValid(CommonTestData.ABHA_ADDRESS_VALID,constraintValidatorContext);
        Assert.assertEquals(false,response);
    }
    @Test
    public void abhaValidatorTest2(){
        Boolean response = abhaValidator.isValid(CommonTestData.ABHA_ADDRESS_INVALID,constraintValidatorContext);
        Assert.assertEquals(false,response);
    }
}
