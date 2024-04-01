package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.FaceDto;
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


import javax.validation.ConstraintValidatorContext;

import static org.mockito.ArgumentMatchers.any;
@ExtendWith(SpringExtension.class)
public class AadhaarNumberFaceValidatorTests {
    @InjectMocks
    AadhaarNumberFaceValidator aadhaarNumberFaceValidator;
    private FaceDto faceDto;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    @Mock
    RSAUtil rsaUtil;
    @BeforeEach
    void setup(){
        faceDto=new FaceDto();

    }
    @AfterEach
    void tearDown(){
        faceDto=null;
    }
    @Test
    public void isValidTest(){
        faceDto.setAadhaar("1234567890as");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("123456789098");
        Boolean result = aadhaarNumberFaceValidator.isValid(faceDto,constraintValidatorContext);
        Assert.assertEquals(result,false);
    }
    @Test
    public void isValidTestE(){
        faceDto.setAadhaar("56570879-887.");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("123456789098");
        Boolean result = aadhaarNumberFaceValidator.isValid(faceDto,constraintValidatorContext);
        Assert.assertEquals(result,false);
    }
    @Test
    public void isValidTest2(){
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("123456789098");
        Boolean result = aadhaarNumberFaceValidator.isValid(faceDto,constraintValidatorContext);
        Assert.assertEquals(result,true);
    }
}
