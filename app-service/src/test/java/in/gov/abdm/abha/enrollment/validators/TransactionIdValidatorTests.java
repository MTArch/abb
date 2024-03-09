package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

@ExtendWith(SpringExtension.class)
public class TransactionIdValidatorTests {
    @InjectMocks
    TransactionIdValidator transactionIdValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    private MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto;
    @BeforeEach
    void setup(){
        mobileOrEmailOtpRequestDto=new MobileOrEmailOtpRequestDto();
    }
    @AfterEach
    void tearDown(){
        mobileOrEmailOtpRequestDto=null;
    }
    @Test
    public void isValid(){
        Assert.assertEquals(true,transactionIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValid2(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL,Scopes.MOBILE_VERIFY,Scopes.CHILD_ABHA_ENROL, Scopes.EMAIL_VERIFY,Scopes.VERIFY_ENROLLMENT));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        Assert.assertEquals(true,transactionIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValid3(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList());
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        Assert.assertEquals(true,transactionIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValid4(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        Assert.assertEquals(true,transactionIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValid5(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY, Scopes.DL_FLOW));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        Assert.assertEquals(true,transactionIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValid6(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY, Scopes.DL_FLOW, Scopes.CHILD_ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.EMAIL);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        mobileOrEmailOtpRequestDto.setTxnId("1");
        Assert.assertEquals(false,transactionIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValid7(){
        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY, Scopes.DL_FLOW, Scopes.CHILD_ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.EMAIL);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        mobileOrEmailOtpRequestDto.setTxnId("");
        Assert.assertEquals(false,transactionIdValidator.isValid(mobileOrEmailOtpRequestDto,constraintValidatorContext));
    }
}
