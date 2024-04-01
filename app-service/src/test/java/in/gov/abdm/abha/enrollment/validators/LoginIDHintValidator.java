package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class LoginIDHintValidator {
    @InjectMocks
    LoginHintValidator loginHintValidator;
    @InjectMocks
    LoginIdValidator loginIdValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    @Mock
    RSAUtil rsaUtil;
    private MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto;
    @BeforeEach
    void setup(){
        mobileOrEmailOtpRequestDto =new MobileOrEmailOtpRequestDto();
    }
    @Test
    public void isValidTest(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL,Scopes.MOBILE_VERIFY,Scopes.CHILD_ABHA_ENROL, Scopes.EMAIL_VERIFY,Scopes.VERIFY_ENROLLMENT));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        Assert.assertEquals(false,loginHintValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTest2(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.MOBILE);
        Assert.assertEquals(false,loginHintValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTest3(){
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.MOBILE);
        Assert.assertEquals(true,loginHintValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTestLogicId(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.MOBILE);
        mobileOrEmailOtpRequestDto.setLoginId("1L");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("");
        Assert.assertEquals(false,loginIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTestLogicId2(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.MOBILE);
        mobileOrEmailOtpRequestDto.setLoginId("1L");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("test");
        Assert.assertEquals(true,loginIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTestLogicId3(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL,Scopes.MOBILE_VERIFY));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.MOBILE);
        mobileOrEmailOtpRequestDto.setLoginId("1L");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("!");
        Assert.assertEquals(false,loginIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTestLogicId4(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL,Scopes.EMAIL_VERIFY));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.MOBILE);
        mobileOrEmailOtpRequestDto.setLoginId("1L");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("!");
        Assert.assertEquals(false,loginIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTestLogicId5(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.CHILD_ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setLoginId("1L");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("!");
        Assert.assertEquals(true,loginIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTestLogicId6(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.CHILD_ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.ABHA_NUMBER);
        mobileOrEmailOtpRequestDto.setLoginId("1L");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("!");
        Assert.assertEquals(false,loginIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTestLogicId7(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.CHILD_ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.ABHA_NUMBER);
        mobileOrEmailOtpRequestDto.setLoginId("!");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("!");
        Assert.assertEquals(true,loginIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidTestLogicId8(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.CHILD_ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.ABHA_NUMBER);
        mobileOrEmailOtpRequestDto.setLoginId("");
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("!");
        Assert.assertEquals(false,loginIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }

}
