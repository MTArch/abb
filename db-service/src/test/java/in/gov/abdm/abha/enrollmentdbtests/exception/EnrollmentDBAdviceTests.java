package in.gov.abdm.abha.enrollmentdbtests.exception;

import in.gov.abdm.abha.enrollmentdb.exception.EnrollmentDBAdvice;
import in.gov.abdm.abha.enrollmentdb.exception.GenericExceptionMessage;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

@ExtendWith(SpringExtension.class)
public class EnrollmentDBAdviceTests {
    @InjectMocks
    EnrollmentDBAdvice enrollmentDBAdvice;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

    }
    @AfterEach
    void tearDown() {

    }
    @Test
    public void runtimeGenericExceptionHandlerTests(){
        Map<String, Object> result =  enrollmentDBAdvice.runtimeGenericExceptionHandler(new GenericExceptionMessage("test"));
        Assert.assertEquals(result.isEmpty(),false);
    }

}
