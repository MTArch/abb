package in.gov.abdm.abha.enrollmentdbtests.enums;

import in.gov.abdm.abha.enrollmentdb.enums.AbhaType;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AbhaTypeTests {
    @Test
    public void isValidTest(){
        Assert.assertEquals(true, AbhaType.isValid(AbhaType.CHILD.toString()));
    }
    @Test
    public void isValidTes2t(){
        Assert.assertEquals(false, AbhaType.isValid("test"));
    }
}
