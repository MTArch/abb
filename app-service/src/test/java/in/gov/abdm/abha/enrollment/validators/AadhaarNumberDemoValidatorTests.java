package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.AssertTrue;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AadhaarNumberDemoValidatorTests {
    @InjectMocks
    AadhaarNumberDemoValidator aadhaarNumberDemoValidator;
    @Mock
    RSAUtil rsaUtil;
    private DemoDto demoDto;
   @Mock
   ConstraintValidatorContext constraintValidatorContext;
    @BeforeEach
    void setup(){
        demoDto=new DemoDto();

    }
    @AfterEach
    void tearDown(){
        demoDto=null;
    }
    @Test
    public void isValidTest(){
        demoDto.setAadhaar("1234567890as");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("123456789098");
        Boolean result = aadhaarNumberDemoValidator.isValid(demoDto,constraintValidatorContext);
        Assert.assertEquals(result,false);
    }
    @Test
    public void isValidTestE(){
        demoDto.setAadhaar("56570879-887.");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("123456789098");
        Boolean result = aadhaarNumberDemoValidator.isValid(demoDto,constraintValidatorContext);
        Assert.assertEquals(result,false);
    }
    @Test
    public void isValidTest2(){
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("123456789098");
        Boolean result = aadhaarNumberDemoValidator.isValid(demoDto,constraintValidatorContext);
        Assert.assertEquals(result,true);
    }
}
