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
public class DLNumberValidatorTests {
    @InjectMocks
    DLNumberValidator dlNumberValidator;
    @InjectMocks
    DocumentValidator documentValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    @Test
    void isValidtest(){
        Assert.assertEquals(true,dlNumberValidator.isValid("2343a212 34543a21",constraintValidatorContext));

    }
    @Test
    void isValidTest(){
        Assert.assertEquals(true,documentValidator.isValid("doc",constraintValidatorContext));
    }
}
