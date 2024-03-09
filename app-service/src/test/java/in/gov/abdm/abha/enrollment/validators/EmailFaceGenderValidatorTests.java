package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.FaceDto;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
public class EmailFaceGenderValidatorTests {
    @InjectMocks
    EmailValidator emailValidator;
    @InjectMocks
    FaceValidator faceValidator;
    @InjectMocks
    GenderValidator genderValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    private AuthData authData;
    private FaceDto faceDto;
    private ArrayList<AuthMethods> authMethods;
    @BeforeEach
    void setup(){
        authData=new AuthData();
        authMethods = new ArrayList<>();
        faceDto=new FaceDto();

    }
    @AfterEach
    void tearDown(){

    }
    @Test
    void emailTest(){
        Assert.assertEquals(true,emailValidator.isValid(CommonTestData.EMAIL_VALID,constraintValidatorContext));
    }
    @Test
    void emailTest2(){
        Assert.assertEquals(true,emailValidator.isValid("",constraintValidatorContext));
    }
    @Test
    void facevalidTest(){
        authMethods.add(AuthMethods.FACE);
        faceDto.setAadhaar(CommonTestData.AADHAR_NUMBER);
        faceDto.setMobile("987722822");
        faceDto.setRdPidData("test");
        authMethods.add(AuthMethods.BIO);
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        Assert.assertEquals(true,faceValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    void facevalidTest2(){

        authMethods.add(AuthMethods.BIO);
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        Assert.assertEquals(true,faceValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    void facevalidTest3(){
        authMethods.add(AuthMethods.FACE);
        authData.setAuthMethods(authMethods);
        authData.setFace(null);
        Assert.assertEquals(false,faceValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    void facevalidTest4(){
        authData.setAuthMethods(null);
        Assert.assertEquals(true,faceValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    void genderTest(){
        Assert.assertEquals(true,genderValidator.isValid("M",constraintValidatorContext));
    }
    @Test
    void genderTest2(){
        Assert.assertEquals(true,genderValidator.isValid("",constraintValidatorContext));
    }
}
