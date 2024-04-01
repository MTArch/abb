package in.gov.abdm.abha.enrollmentdbtests.domain.transaction;

import in.gov.abdm.abha.enrollmentdb.domain.transaction.TransactionServiceImpl;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import in.gov.abdm.abha.enrollmentdb.repository.TransactionRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
public class TransactionServiceImplTests {
    @InjectMocks
    TransactionServiceImpl transactionService;
    @Mock
    TransactionRepository transactionRepository;

    @Mock
    private ModelMapper modelMapper;
    private TransactionDto transactionDto;
    private Transection transection;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        transactionDto=new TransactionDto();
        TransactionDto transectiond=new TransactionDto(1L,"",1,"","","","", LocalDateTime.now(),"","","","","","","","","",true,"","","",true,"","","",1,"","","","","","", UUID.randomUUID(),"","","","","","","","","","","","","","","","","",true,"","","","",true);
        Transection t=new Transection(1L,"",1,"","","","", LocalDateTime.now(),"","","","","","","","",true,"","","",true,"","","",1,"","","","","","", UUID.randomUUID(),"","","","","","","","","","","","","","","","","",true,"","","","",true);
        transection=new Transection(t.getId(),t.getAadharNo(),t.getAadharRetryCount(),t.getAadharTxn(),t.getAccType(),t.getAddress(),t.getCo(),
        t.getCreatedDate(),t.getDayOfBirth(),t.getDistrictName(),t.getEmail(),t.getGender(),t.getHouse(),t.getKycdob(),t.getKycReason(),t.getKycStatus(),t.isKycVerified(),t.getLm(),t.getLoc(),t.getMobile(),t.isMobileVerified(),t.getMonthOfBirth(),t.getName(),
                t.getOtp(),t.getOtpRetryCount(),t.getPincode(),t.getPo(),t.getStateName(),t.getStatus(),t.getSubDistrictName(),t.getTownName(),t.getTxnId(),t.getType(),t.getVillageName(),t.getWardName(),t.getXmluid(),t.getYearOfBirth(),t.getResponseCode(),t.getCodeChallenge(),t.getCodeChallengeMethod(),t.getOidcActionType(),t.getOidcClientId(),t.getOidcRedirectUrl(),t.getResponseType(),t.getScope(),t.getState(),t.getKycType(),t.getClientIp(),t.getPhrAddress(),t.isEmailVerified(),t.getDocumentCode(),t.getLoginModeType(),t.getTxnResponse(),t.getHealthIdNumber(),t.isNewTransaction());
        transection=new Transection();
        transactionDto.setKycPhoto("kycphoto");
        transactionDto.setId(1L);
        transection.setId(1L);
        transection.isNew();
    }
    @AfterEach
    void tearDown() {
        transactionDto=null;
        transection=null;
    }
    @Test
    public void publishPhrUserPatientEventTests(){
        Mockito.when(modelMapper.map(any(TransactionDto.class),any())).thenReturn(transection);
        Mockito.when(transactionRepository.save(any())).thenReturn(Mono.just(transection));
        Mockito.when(transactionRepository.updateKycPhoto(any(),anyLong())).thenReturn(Mono.just(transactionDto));
        StepVerifier.create(transactionService.createTransaction(transactionDto)).expectNext(transactionDto).verifyComplete();

    }
    @Test
    public void publishPhrUserPatientEventTests2(){
        Mockito.when(modelMapper.map(any(TransactionDto.class),any())).thenReturn(transection);
        Mockito.when(transactionRepository.save(any())).thenReturn(Mono.just(transection));
        Mockito.when(transactionRepository.updateKycPhoto(any(),anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(transactionService.createTransaction(transactionDto)).expectNextCount(1L).verifyComplete();
        transactionDto.setKycPhoto(null);
        StepVerifier.create(transactionService.createTransaction(transactionDto)).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getTransactionTests(){
        Mockito.when(modelMapper.map(any(Transection.class),any())).thenReturn(transactionDto);
        Mockito.when(transactionRepository.findById(anyLong())).thenReturn(Mono.just(transection));
        Mockito.when(transactionRepository.getProfilePhoto(anyLong())).thenReturn(Mono.just("test"));
        StepVerifier.create(transactionService.getTransaction(1L)).expectNext(transactionDto).verifyComplete();

    }
    @Test
    public void getTransactionTests2(){
        Mockito.when(modelMapper.map(any(Transection.class),any())).thenReturn(transactionDto);
        Mockito.when(transactionRepository.findById(anyLong())).thenReturn(Mono.just(transection));
        Mockito.when(transactionRepository.getProfilePhoto(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(transactionService.getTransaction(1L)).expectNext(transactionDto).verifyComplete();

    }
    @Test
    public void getTransactionByTxnIdTests(){
        Mockito.when(modelMapper.map(any(Transection.class),any())).thenReturn(transactionDto);
        Mockito.when(transactionRepository.findByTxnId(any(),any(),any())).thenReturn(Mono.just(transection));
        Mockito.when(transactionRepository.getProfilePhoto(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(transactionService.getTransactionByTxnId("Test")).expectNext(transactionDto).verifyComplete();

    }
    @Test
    public void updateTransactionByIdTests(){
        Mockito.when(modelMapper.map(any(TransactionDto.class),any())).thenReturn(transection);
        Mockito.when(transactionRepository.updateKycPhoto(any(),anyLong())).thenReturn(Mono.just(transactionDto));
        Mockito.when(transactionRepository.save(any())).thenReturn(Mono.just(transection));
        Mockito.when(transactionRepository.getProfilePhoto(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(transactionService.updateTransactionById(transactionDto,"1")).expectNext(transactionDto).verifyComplete();

    }
    @Test
    public void deleteTransactionByTxnIdTests(){
        Mockito.when(modelMapper.map(any(TransactionDto.class),any())).thenReturn(transection);
        Mockito.when(transactionRepository.deleteByTxnId(any())).thenReturn(Mono.empty());
       // Mockito.when(transactionRepository.save(any())).thenReturn(Mono.just(transection));
       // Mockito.when(transactionRepository.getProfilePhoto(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(transactionService.deleteTransactionByTxnId("1")).expectNextCount(0L).verifyComplete();

    }
}
