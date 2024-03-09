package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
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
import java.util.Arrays;
import java.util.Collections;

@ExtendWith(SpringExtension.class)
public class AuthMethodValidatorTests {
    @InjectMocks
    AuthMethodValidator authMethodValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    private AuthData authData;
    @BeforeEach
    void setup(){
        authData=new AuthData();
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.BIO);
        authData.setAuthMethods(authMethods);
    }
    @AfterEach
    void tearDown(){

    }
    @Test
    void isValidtest(){
        Boolean res = authMethodValidator.isValid(authData,constraintValidatorContext);
        Assert.assertEquals(true,res);
    }
}
