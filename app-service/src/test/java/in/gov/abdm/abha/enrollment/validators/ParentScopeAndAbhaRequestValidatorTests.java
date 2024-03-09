package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
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

@ExtendWith(SpringExtension.class)
public class ParentScopeAndAbhaRequestValidatorTests {
    @InjectMocks
    ParentAbhaRequestValidator parentAbhaRequestValidator;
    @InjectMocks
    ParentScopeValidator parentScopeValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    private LinkParentRequestDto linkParentRequestDto;
    @BeforeEach
    void setup(){
        linkParentRequestDto=new LinkParentRequestDto();
    }
    @AfterEach
    void tearDown(){
        linkParentRequestDto=null;
    }
    @Test
    public void isValidparentAbhaRequest(){
        Assert.assertEquals(false,parentAbhaRequestValidator.isValid(linkParentRequestDto,constraintValidatorContext));
    }
    @Test
    public void isValidParentScope(){
        Assert.assertEquals(false,parentScopeValidator.isValid(linkParentRequestDto,constraintValidatorContext));
    }
}
