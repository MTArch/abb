package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.TOKEN_VALID;

@ExtendWith(SpringExtension.class)
public class RequestMapperTests {
    @Mock
    JWTUtil jwtUtil;
    /*@Test
    public void prepareRequestHeadersTest(){
       RequestHeaders req= RequestMapper.prepareRequestHeaders("name","Bearer \\"+"\\.auth\\"+"\\.clientId.\\"+".",TOKEN_VALID,TOKEN_VALID);
        Assert.assertNotNull(req);
       // RequestHeaders req= RequestMapper.prepareRequestHeaders("name","Bearer \\"+"\\.auth"+" "+"\\.clientId"+".",null);
    }*/
    @Test
    public void prepareRequestHeadersTestErr(){
        Assert.assertThrows(IllegalArgumentException.class,()->RequestMapper.prepareRequestHeaders("name","Bearer \\"+"\\.auth\\"+"\\.clientId.\\"+".",TOKEN_VALID,""));
        }
}
