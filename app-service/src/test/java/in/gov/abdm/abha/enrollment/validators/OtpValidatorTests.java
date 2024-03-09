package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class OtpValidatorTests {
    @InjectMocks
    OtpValidator otpValidator;
    @InjectMocks
    OtpSystemValidator otpSystemValidator;
    @InjectMocks
    OtpValueValidator otpValueValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    @Mock
    RSAUtil rsaUtil;
    private AuthData authData;
    private OtpDto otpDto;
    private MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto;
    @BeforeEach
    void setup(){
        mobileOrEmailOtpRequestDto=new MobileOrEmailOtpRequestDto();
        String s = mobileOrEmailOtpRequestDto.toString();
        authData=new AuthData();
        otpDto = new OtpDto();

    }
    @AfterEach
    void tearDown(){
        otpDto=null;
        mobileOrEmailOtpRequestDto=null;
    }
    @Test
    void isValidtest(){
        otpDto.setOtpValue("232323");
        otpDto.setMobile("9872722828");
        otpDto.setTxnId("1");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.OTP);
        authData.setAuthMethods(authMethods);
        authData.setOtp(otpDto);
        Boolean res = otpValidator.isValid(authData,constraintValidatorContext);
        Assert.assertEquals(true,res);
    }
    @Test
    void isValidtest2(){
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.OTP);
        authData.setAuthMethods(authMethods);
        Boolean res = otpValidator.isValid(authData,constraintValidatorContext);
        Assert.assertEquals(false,res);
    }
    @Test
    void isValidtest3(){
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.BIO);
        authMethods.add(AuthMethods.FACE);
        authData.setAuthMethods(authMethods);
        Boolean res = otpValidator.isValid(authData,constraintValidatorContext);
        Assert.assertEquals(true,res);
    }
    @Test
    void isValidtest4(){
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authData.setAuthMethods(authMethods);
        Boolean res = otpValidator.isValid(authData,constraintValidatorContext);
        Assert.assertEquals(true,res);
    }
    @Test
    void isValidtest5(){
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authData.setAuthMethods(authMethods);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("a");
        Boolean res = otpValueValidator.isValid("162a77",constraintValidatorContext);
        Assert.assertEquals(false,res);
    }
    @Test
    void isValidtest6(){
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authData.setAuthMethods(authMethods);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("a");
        Boolean res = otpValueValidator.isValid("!",constraintValidatorContext);
        Assert.assertEquals(false,res);
    }
    @Test
    void isValidtest7(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL,Scopes.MOBILE_VERIFY,Scopes.CHILD_ABHA_ENROL, Scopes.EMAIL_VERIFY,Scopes.VERIFY_ENROLLMENT));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authData.setAuthMethods(authMethods);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("a");
        Boolean res = otpSystemValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext);
        Assert.assertEquals(false,res);
    }
    @Test
    void isValidtest8(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL,Scopes.MOBILE_VERIFY,Scopes.CHILD_ABHA_ENROL, Scopes.EMAIL_VERIFY,Scopes.VERIFY_ENROLLMENT));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.ABDM);
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authData.setAuthMethods(authMethods);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("a");
        Boolean res = otpSystemValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext);
        Assert.assertEquals(false,res);
    }
    @Test
    void isValidtest9(){
       // mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL,Scopes.MOBILE_VERIFY,Scopes.CHILD_ABHA_ENROL, Scopes.EMAIL_VERIFY,Scopes.VERIFY_ENROLLMENT));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.ABDM);
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authData.setAuthMethods(authMethods);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("a");
        Boolean res = otpSystemValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext);
        Assert.assertEquals(true,res);
    }

}
