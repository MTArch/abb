package in.gov.abdm.abha.enrollment.utilities;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AadhaarUtilityTests {
    @Test
    void test(){
        String res = AadhaarUtility.getPhoneNumber("******3423");
        Assert.assertEquals(null,res);
    }
}
