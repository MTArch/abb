package in.gov.abdm.abha.enrollment.aadhaar.send_otp;

import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.phr.User;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.AbhaAddressService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.impl.AbhaAddressServiceImpl;
import in.gov.abdm.abha.enrollment.services.idp.IdpAppService;
import in.gov.abdm.abha.enrollment.services.phr.PhrDbService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AbhaAddressServiceTests {

    @InjectMocks
    AbhaAddressServiceImpl abhaAddressService;

    @Mock
    TransactionService transactionService;
    @Mock
    AccountService accountService;
    @Mock
    PhrDbService phrDbService;
    @Mock
    IdpAppService idpAppService;
    @Mock
    HidPhrAddressService hidPhrAddressService;

    private TransactionDto transactionDto;
    private AccountDto accountDto;
    private User user;
    private AbhaAddressRequestDto abhaAddressRequestDto;
    private HidPhrAddressDto hidPhrAddressDto;


    @BeforeEach
    void setup()
    {
        transactionDto = new TransactionDto();
        accountDto = new AccountDto();
        user = new User();
        abhaAddressRequestDto = new AbhaAddressRequestDto();
        hidPhrAddressDto = new HidPhrAddressDto();
    }

    @AfterEach
    void tearDown()
    {
        transactionDto = null;
        accountDto = null;
        user = null;
        abhaAddressRequestDto = null;
        hidPhrAddressDto = null;
    }

    @Test
    void getAbhaAddressSuccess()
    {
        transactionDto.setHealthIdNumber("91-3761-8271-7425");
        transactionDto.setTxnId(UUID.fromString("8c1eb948-2818-4d21-b942-8a26b45e5e8d"));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        accountDto.setFirstName("Anchal");
        accountDto.setLastName("Singh");
        accountDto.setDayOfBirth("29");
        accountDto.setMonthOfBirth("09");
        accountDto.setYearOfBirth("1995");
        when(accountService.getAccountByHealthIdNumber(any()))
                .thenReturn(Mono.just(accountDto));
        when(phrDbService.getUsersByAbhaAddressList(anyList()))
                .thenReturn(Flux.just(user));

        String txnId = "8c1eb948-2818-4d21-b942-8a26b45e5e8d";
        StepVerifier.create(abhaAddressService.getAbhaAddress(txnId))
                .expectNextCount(1L).verifyComplete();
    }

    @Test
    void createAbhaAddressSuccess()
    {
        transactionDto.setHealthIdNumber("91-3761-8271-7425");
        transactionDto.setTxnId(UUID.fromString("8c1eb948-2818-4d21-b942-8a26b45e5e8d"));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByHealthIdNumber(any()))
                .thenReturn(Mono.just(accountDto));
        when(idpAppService.verifyAbhaAddressExists(any()))
                .thenReturn(Mono.just(false));
        when(hidPhrAddressService.findByHealthIdNumber(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.updateHidPhrAddressById(any(),any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        hidPhrAddressDto.setPhrAddress("anchal2909@abdm");
        hidPhrAddressDto.setHealthIdNumber("91-3761-8271-7425");
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        abhaAddressRequestDto.setPreferredAbhaAddress("anchal2909@abdm");
        abhaAddressRequestDto.setPreferred("1");
        StepVerifier.create(abhaAddressService.createAbhaAddress(abhaAddressRequestDto))
                .expectNextCount(1L).verifyComplete();
    }
}
