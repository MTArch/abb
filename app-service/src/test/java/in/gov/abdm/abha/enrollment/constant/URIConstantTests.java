package in.gov.abdm.abha.enrollment.constant;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class URIConstantTests {
    @InjectMocks
    URIConstant uriConstant;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown(){

    }
    @Test
    public void excludedListTests(){
        String ex;
        for (String s : URIConstant.excludedList) {
            ex=s;
        }

    }
}
