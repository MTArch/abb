package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.validators.request.HelperUtil;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class ValidatorsTests {
    @InjectMocks
    PreferredValidator preferredValidator;
    @InjectMocks
    RelationshipValidator relationshipValidator;
    @InjectMocks
    ScopeValidator scopeValidator;
    @InjectMocks
    TimestampDemoValidator timestampDemoValidator;
    @InjectMocks
    TimestampOtpValidator timestampOtpValidator;
    @InjectMocks
    YearOfBirthValidator yearOfBirthValidator;
    @InjectMocks
    ChildAbhaRequestValidator childAbhaRequestValidator;
    @InjectMocks
    ChildAuthOtpValidator childAuthOtpValidator;
    @InjectMocks
    ChildAuthMethodsValidator childAuthMethodsValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    private List<Scopes> requestScopes;
    private ArrayList<AuthMethods> authMethods;
    private DemoDto demoDto;
    private AuthData authData;
    private LinkParentRequestDto linkParentRequestDto;
    private OtpDto otpDto;

    @BeforeEach
    void setup(){
        requestScopes=new ArrayList<>();
        String s = Scopes.WRONG.toString();
        requestScopes.add(Scopes.PARENT_ABHA_LINK);
        linkParentRequestDto=new LinkParentRequestDto();
        otpDto = new OtpDto();
        demoDto=new DemoDto();
        demoDto.setTimestamp("2024-12-12 12:12:12");
        demoDto.setMobile("");
        demoDto.setAadhaar("");
        demoDto.setName("");
        demoDto.setYob("");
        demoDto.setGender("");
        demoDto.setNameMatchStrategy("");
        DemoDto demoDto1= new DemoDto();
        demoDto1.setNameMatchStrategy(demoDto.getNameMatchStrategy());
        demoDto1.setTimestamp(demoDto.getTimestamp());
        demoDto1.setMobile(demoDto.getMobile());
        demoDto1.setYob(demoDto.getYob());
        demoDto1.setGender(demoDto.getGender());
        demoDto1.setName(demoDto.getName());
        demoDto1.setAadhaar(demoDto.getAadhaar());
        authData=new AuthData();
        authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.BIO);
        authData.setAuthMethods(authMethods);
        //linkParentRequestDto=new LinkParentRequestDto();
    }
    @AfterEach
    void tearDown(){
        requestScopes=null;
        authData=null;
        demoDto=null;
        linkParentRequestDto=null;
        otpDto=null;

    }
    @Test
    public void isValidpreferredValidator(){
        Assert.assertEquals(true,preferredValidator.isValid("1",constraintValidatorContext));
    }
    @Test
    public void isValidRelationship(){
        Assert.assertEquals(true,relationshipValidator.isValid(Relationship.FATHER,constraintValidatorContext));
    }
    @Test
    public void isValidRScope(){
        Assert.assertEquals(true,scopeValidator.isValid(requestScopes,constraintValidatorContext));
    }
    @Test
    public void isValidRScope2(){
        requestScopes=new ArrayList<>();
        Assert.assertEquals(false,scopeValidator.isValid(requestScopes,constraintValidatorContext));
    }
    @Test
    public void isValidTimestampDemoValidator(){
        Assert.assertEquals(false,timestampDemoValidator.isValid(demoDto,constraintValidatorContext));
    }
    @Test
    public void isValidTimestampDemoValidator2(){
        demoDto.setTimestamp(null);
        Assert.assertEquals(true,timestampDemoValidator.isValid(demoDto,constraintValidatorContext));
    }
    @Test
    public void isValidTimestampDemoValidatorCatchBlock(){
        demoDto.setTimestamp(LocalDateTime.now().toString());
        Assert.assertEquals(false,timestampDemoValidator.isValid(demoDto,constraintValidatorContext));
    }
    @Test
    public void isValidTimestampOtpValidator(){
        Assert.assertEquals(false,timestampOtpValidator.isValid("2024-03-05 10:12:12", constraintValidatorContext));
    }
    @Test
    public void isValidTimestampOtpValidator2(){
        Assert.assertEquals(true,timestampOtpValidator.isValid("", constraintValidatorContext));
    }
    @Test
    public void isValidTimestampOtpValidatorcatchBlock(){
        Assert.assertEquals(false,timestampOtpValidator.isValid(LocalDateTime.now().toString(), constraintValidatorContext));
    }
    @Test
    public void isValidYearOfBirth(){
        Assert.assertEquals(true,yearOfBirthValidator.isValid("2000",constraintValidatorContext));
    }
    @Test
    public void isValidYearOfBirth2(){
        Assert.assertEquals(false,yearOfBirthValidator.isValid("0000",constraintValidatorContext));
    }
    @Test
    public void isValidYearOfBirthCatchBlock(){
        Assert.assertEquals(false,yearOfBirthValidator.isValid("!",constraintValidatorContext));
    }
    @Test
    public void isValidYearOfBirth3(){
        Assert.assertEquals(true,yearOfBirthValidator.isValid("",constraintValidatorContext));
    }
    @Test
    public void isValidChildAuthOtpValidator(){
        Assert.assertEquals(true,childAuthOtpValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    public void isValidChildAuthOtpValidator2(){
        authMethods.add(AuthMethods.OTP);
        otpDto.setTxnId("1");
        otpDto.setOtpValue("123442");
        authData.setOtp(otpDto);
        Assert.assertEquals(true,childAuthOtpValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    public void isValidChildAuthOtpValidator3(){
        authMethods.add(AuthMethods.OTP);
        authData.setOtp(otpDto);
        Assert.assertEquals(false,childAuthOtpValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    public void isValidChildAuthOtpValidator4(){
        authData.setAuthMethods(null);
        Assert.assertEquals(true,childAuthOtpValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    public void isValidChildAuthMethodsValidator(){
        Assert.assertEquals(true,childAuthMethodsValidator.isValid(authData,constraintValidatorContext));
    }
    @Test
    public void isValidChildAbhaRequestValidator(){
        Assert.assertEquals(false,childAbhaRequestValidator.isValid(linkParentRequestDto,constraintValidatorContext));
    }

}
