package in.gov.abdm.abha.enrollment.aadhaar.send_otp.enrol.abha_address;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.client.AbhaDBHidPhrAddressFClient;
import in.gov.abdm.abha.enrollment.client.AbhaDBTransactionFClient;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.AbhaAddressResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account.impl.AccountServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.impl.HidPhrAddressServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.database.transaction.impl.TransactionServiceImpl;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.impl.AbhaAddressServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AbhaAddressServiceImplTest {

    @InjectMocks
    AbhaAddressServiceImpl abhaAddressService;

    @Mock
    TransactionService transactionService;

    @Mock
    AccountService accountService;

    @Mock
    HidPhrAddressService hidPhrAddressService;

    @Mock
    AbhaDBTransactionFClient abhaDBTransactionFClient;

    @Mock
    AbhaDBAccountFClient abhaDBAccountFClient;

    @Mock
    AbhaDBHidPhrAddressFClient abhaDBHidPhrAddressFClient;

    private  SuggestAbhaResponseDto suggestAbhaResponseDto;
    private TransactionDto transactionDto;
    private List<String> listAbhaSuggestion;
    private AccountDto accountDto;
    private String TXN_ID ="daa3ec62-60b2-4d06-8dc0-16e38c38d102";

    private AbhaAddressRequestDto abhaAddressRequestDto;
    private AbhaAddressResponseDto abhaAddressResponseDto;
    private HidPhrAddressDto hidPhrAddressDto;

    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
       suggestAbhaResponseDto = new SuggestAbhaResponseDto();
       transactionDto = new TransactionDto();
       transactionDto.setTxnId(UUID.fromString("daa3ec62-60b2-4d06-8dc0-16e38c38d102"));
       listAbhaSuggestion = new LinkedList<>();
       accountDto = new AccountDto();
       accountDto.setHealthIdNumber("91-2811-0057-3118");

       abhaAddressRequestDto = new AbhaAddressRequestDto();
       abhaAddressResponseDto= new AbhaAddressResponseDto();
       abhaAddressResponseDto.setTxnId("daa3ec62-60b2-4d06-8dc0-16e38c38d102");
       abhaAddressResponseDto.setHealthIdNumber("91-2811-0057-3118");
       abhaAddressResponseDto.setPreferredAbhaAddress("gsh79");
       hidPhrAddressDto = new HidPhrAddressDto();
    }
    @AfterEach
    void afterEach(){
        suggestAbhaResponseDto= null;
        transactionDto = null;
        listAbhaSuggestion = null;
        accountDto= null;
        abhaAddressRequestDto= null;
        abhaAddressResponseDto =  null;
        hidPhrAddressDto = null;
    }

  /*  @Test
    void TestGetAbhaAddress(){
     when(transactionService.findTransactionDetailsFromDB(anyString())).thenReturn(Mono.just(new TransactionDto()));
     when(accountService.getAccountByHealthIdNumber(anyString())).thenReturn(Mono.just(accountDto));
     when(hidPhrAddressService.findByPhrAddressIn(listAbhaSuggestion)).thenReturn(Flux.just(hidPhrAddressDto));
        StepVerifier
                .create(abhaAddressService.getAbhaAddress(anyString()))
                .expectNextCount(0L)
                .expectNext(suggestAbhaResponseDto)
                .verifyComplete();
    }*/
 /*   @Test
    void TestCreateAbhaAddress(){
        when(transactionService.findTransactionDetailsFromDB(anyString())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByHealthIdNumber(anyString())).thenReturn(Mono.just(accountDto.));
        when(hidPhrAddressService.getPhrAddressByPhrAddress(anyString())).thenReturn(Mono.just(hidPhrAddressDto));
        abhaAddressRequestDto.setTxnId("daa3ec62-60b2-4d06-8dc0-16e38c38d102");

        StepVerifier
                .create(abhaAddressService.createAbhaAddress(abhaAddressRequestDto))
//                .expectNextCount(0L)
//                .expectNext(abbh.setHealthIdNumber(""));
                .expectNext(abhaAddressResponseDto)
                .verifyComplete();

    }*/

    //        when(abhaDBTransactionFClient.getTransactionByTxnId(anyString())).thenReturn(Mono.just(transactionDto));
    //        when(abhaDBAccountFClient.getAccountByHealthIdNumber(anyString())).thenReturn(Mono.just(new AccountDto()));
    //        when(abhaDBHidPhrAddressFClient.getPhrAddress(anyString())).thenReturn(Mono.just(new HidPhrAddressDto()));
    //        abhaAddressRequestDto.setPreferredAbhaAddress("91-2811-0057-3118");
//        abhaAddressRequestDto.setPreferred(1);
}
