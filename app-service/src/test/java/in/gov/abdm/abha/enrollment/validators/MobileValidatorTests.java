package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import liquibase.pro.packaged.D;
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
public class MobileValidatorTests {
    @InjectMocks
    MobileValidator mobileValidator;
    @InjectMocks
    MobileFieldValidator mobileFieldValidator;
    @InjectMocks
    NameValidator nameValidator;
    @Mock
    RSAUtil rsaUtil;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    private DemoDto demoDto;
    @BeforeEach
    void setup(){
        demoDto=new DemoDto();
    }
    @AfterEach
    void tearDown(){
        demoDto=null;
    }
    @Test
    public void isValid(){

        demoDto.setMobile("9873822s93");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("9877738383");
        Assert.assertEquals(true,mobileValidator.isValid(demoDto,constraintValidatorContext));
    }
    @Test
    public void isValid2(){

        demoDto.setMobile("!!!");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("9877738383");
        Assert.assertEquals(false,mobileValidator.isValid(demoDto,constraintValidatorContext));
    }
    @Test
    public void isValid3(){

        Mockito.when(rsaUtil.decrypt(any())).thenReturn("9877738383");
        Assert.assertEquals(true,mobileValidator.isValid(demoDto,constraintValidatorContext));
    }
    @Test
    public void isValid4(){
        Assert.assertEquals(true,mobileFieldValidator.isValid("9873829922",constraintValidatorContext));
    }
    @Test
    public void isValid5(){
        Assert.assertEquals(true,mobileFieldValidator.isValid("",constraintValidatorContext));
    }
    @Test
    public void isValid6(){
        Assert.assertEquals(true,nameValidator.isValid("name",constraintValidatorContext));
    }
    @Test
    public void isValid7(){
        Assert.assertEquals(true,nameValidator.isValid("",constraintValidatorContext));
    }


}
