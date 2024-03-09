package in.gov.abdm.abha.enrollmentdbtests.domain.procedure;

import in.gov.abdm.abha.enrollmentdb.domain.kafka.KafkaService;
import in.gov.abdm.abha.enrollmentdb.domain.procedure.ProcedureServiceImpl;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import in.gov.abdm.abha.enrollmentdb.repository.procedure.ProcedureRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ProcedureServiceImplTests {
    @InjectMocks
    ProcedureServiceImpl procedureService;
    @Mock
    ProcedureRepository procedureRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    KafkaService kafkaService;

    @Mock
    HidPhrAddressRepository hidPhrAddressRepository;
    private SaveAllDataRequest saveAllDataRequest;
    private Accounts accounts;
    private HidPhrAddress hidPhrAddress;
    private AccountAuthMethods accountAuthMethods;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        saveAllDataRequest =new SaveAllDataRequest();
        accounts=new Accounts();
        accounts.setHealthId("12321");
        hidPhrAddress=new HidPhrAddress();
        accountAuthMethods=new AccountAuthMethods();
        List<Accounts> listAccounts = new ArrayList<>();
        listAccounts.add(accounts);
        List<HidPhrAddress> listHidPhrAddress = new ArrayList<>();
        listHidPhrAddress.add(hidPhrAddress);
        List<AccountAuthMethods> listAccountAuthMethods = new ArrayList<>();
        listAccountAuthMethods.add(accountAuthMethods);
        saveAllDataRequest.setAccounts(listAccounts);
        saveAllDataRequest.setHidPhrAddress(listHidPhrAddress);
        saveAllDataRequest.setAccountAuthMethods(listAccountAuthMethods);

    }
    @AfterEach
    void tearDown() {
        saveAllDataRequest=null;
        accounts=null;
        hidPhrAddress=null;
        accountAuthMethods=null;
    }
    @Test
    public void publishPhrUserPatientEventTests(){
        Mockito.when(procedureRepository.saveAllDataProcedure(any(),any(),any())).thenReturn(Mono.just("success"));
        Mockito.when(hidPhrAddressRepository.getPhrAddressByPhrAddress(any())).thenReturn(Mono.just(hidPhrAddress));
        Mockito.when(kafkaService.publishPhrUserPatientEvent(any())).thenReturn(Mono.empty());
        StepVerifier.create(procedureService.saveAllData(saveAllDataRequest)).expectNext("success").verifyComplete();

    }
}
