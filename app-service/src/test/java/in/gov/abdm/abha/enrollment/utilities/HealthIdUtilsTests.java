package in.gov.abdm.abha.enrollment.utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class HealthIdUtilsTests {
    @Test
    public void actualHealthIdNumberTest(){
        String res =HealthIdUtils.actualHealthIdNumber("12345678901243");
    }
}
