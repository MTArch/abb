package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.client.LgdAppFClient;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class LgdUtilityTests {
    @InjectMocks
    LgdUtility lgdUtility;
    @Mock
    LgdAppFClient lgdAppFClient;

    @Test
    public void getLgdDataTest(){
        Mockito.when(lgdAppFClient.getDetailsByAttribute(any(),any(),any(),any()))
                .thenReturn(Mono.just(Arrays.asList(new LgdDistrictResponse("431010","1","Pune","2","Maharashtra"))));
        Mockito.when(lgdAppFClient.getDetailsByAttributeState(any(),any(),any()))
                .thenReturn(Mono.just(Arrays.asList(new LgdDistrictResponse("431010","1","Pune","2","Maharashtra"))));

        StepVerifier.create(lgdUtility.getLgdData("431010","Maharashtra"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void getLgdDataTest5(){
        Mockito.when(lgdAppFClient.getDetailsByAttribute(any(),any(),any(),any()))
                .thenReturn(Mono.empty());
        Mockito.when(lgdAppFClient.getDetailsByAttributeState(any(),any(),any()))
                .thenReturn(Mono.empty());
        StepVerifier.create(lgdUtility.getLgdData("431010","Maharashtra"))
                .expectNextCount(0L)
                .verifyComplete();
    }
    @Test
    public void getLgdDataTest2(){
        Mockito.when(lgdAppFClient.getDetailsByAttribute(any(),any(),any(),any()))
                .thenReturn(Mono.just(Arrays.asList(new LgdDistrictResponse("431010","1","Pune","2","Maharashtra"))));
        Mockito.when(lgdAppFClient.getDetailsByAttributeState(any(),any(),any()))
                .thenReturn(Mono.just(Arrays.asList(new LgdDistrictResponse("431010","1","Pune","2","Maharashtra"))));

        StepVerifier.create(lgdUtility.getLgdData("",""))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void getLgdDataTest3(){
        StepVerifier.create(lgdUtility.getLgdData(null,null))
                .expectNextCount(0L)
                .verifyComplete();
    }
    @Test
    public void getLgdDataTesterr(){
        Mockito.when(lgdAppFClient.getDetailsByAttribute(any(),any(),any(),any()))
                .thenReturn(Mono.error(Exception::new));
        Mockito.when(lgdAppFClient.getDetailsByAttributeState(any(),any(),any()))
                .thenReturn(Mono.error(Exception::new));
        StepVerifier.create(lgdUtility.getLgdData("431010","Maharashtra"))
                .expectError()
                .verify();
    }
    @Test
    public void getDistrictCodeTest(){
        Mockito.when(lgdAppFClient.getByDistrictCode(any(),any(),any()))
                .thenReturn(Mono.just(Arrays.asList(new LgdDistrictResponse("431010","1","Pune","2","Maharashtra"))));
        StepVerifier.create(lgdUtility.getDistrictCode("1"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void getDistrictCodeTestErr(){
        Mockito.when(lgdAppFClient.getByDistrictCode(any(),any(),any()))
                .thenReturn(Mono.error(Exception::new));
        StepVerifier.create(lgdUtility.getDistrictCode("1"))
                .expectError()
                .verify();
    }
}
