package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.ABHA_NUMBER;

@ExtendWith(SpringExtension.class)
public class AbhaAddressGeneratorTests {
    @InjectMocks
    AbhaAddressGenerator abhaAddressGenerator;

    @Test
    public void generateDefaultAbhaAddressTests(){
        ReflectionTestUtils.setField(abhaAddressGenerator,"abhaAddressExtension","test");
        Assert.assertEquals(ABHA_NUMBER+"@test",abhaAddressGenerator.generateDefaultAbhaAddress(ABHA_NUMBER));
    }
}
