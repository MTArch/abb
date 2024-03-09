package in.gov.abdm.abha.enrollment.phr;

import in.gov.abdm.abha.enrollment.client.PhrDbFClient;
import in.gov.abdm.abha.enrollment.model.phr.User;
import in.gov.abdm.abha.enrollment.services.phr.PhrDbService;
import in.gov.abdm.phr.enrollment.address.Address;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class PhrDbServiceTests {
    @InjectMocks
    PhrDbService phrDbService;

    @Mock
    PhrDbFClient phrDbFClient;

    private User user;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user= User.builder().build();
        user=new User(1L,"","","","","","","","","","","","","","","","","","","",true,true, Timestamp.from(Instant.now()),"",Timestamp.from(Instant.now()),"",true,"",new Address());
        user = new User();
    }
    @AfterEach
    void tearDown(){
        user=null;
    }
    @Test
    public void getUsersByAbhaAddressListTests(){
        List<String> addrList = new ArrayList<>();
        addrList.add("addr1");
        Mockito.when(phrDbFClient.getUsersByAbhaAddressList(any(),any(),any()))
                .thenReturn(Flux.just(user));
        StepVerifier.create(phrDbService.getUsersByAbhaAddressList(addrList))
                .expectNext(new User())
                .verifyComplete();

    }
}
