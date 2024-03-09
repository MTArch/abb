package in.gov.abdm.abha.enrollment.services.auth;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarAuthOtpDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.impl.AadhaarAppServiceImpl;
import in.gov.abdm.abha.enrollment.services.auth.aadhaar.AuthByAadhaarService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.Signature;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthByAadharServiceTests {

    @InjectMocks
    AuthByAadhaarService authByAadhaarService;

    @Mock
    TransactionService transactionService;
    @Mock
    AccountService accountService;

    @Mock
    AadhaarAppServiceImpl aadhaarAppService;

    @Mock
    HidPhrAddressService hidPhrAddressService;
    private AuthRequestDto authByAadhaarRequestDto;
    private AccountDto accountDto;
    private AadhaarUserKycDto aadhaarUserKycDto;
    private AadhaarResponseDto aadhaarResponseDto;
    private AuthData authData;
    private OtpDto otpDto;
    private TransactionDto transactionDto;
    private AuthResponseDto authResponseDto;
    private HidPhrAddressDto hidPhrAddressDto;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        authByAadhaarRequestDto = new AuthRequestDto();
        accountDto = new AccountDto();
        aadhaarUserKycDto = new AadhaarUserKycDto();
        aadhaarResponseDto = new AadhaarResponseDto();
        otpDto = new OtpDto();
        authData = new AuthData();
        transactionDto = new TransactionDto();
        authResponseDto = new AuthResponseDto();
        hidPhrAddressDto = new HidPhrAddressDto();

        ArrayList<AuthMethods> list = new ArrayList<>();
        list.add(AuthMethods.OTP);
        list.add(AuthMethods.BIO);
        list.add(AuthMethods.FACE);
        list.add(AuthMethods.DEMO_AUTH);
        list.add(AuthMethods.DEMO);
        otpDto.setOtpValue("123456");
        otpDto.setTxnId("1");
        authData = new AuthData();
        authData.setAuthMethods(list);
        authData.setOtp(otpDto);
        ArrayList<Scopes> scopes= new ArrayList<>();
        scopes.add(Scopes.ABHA_ENROL);
        scopes.add(Scopes.MOBILE_VERIFY);
        authByAadhaarRequestDto.setAuthData(authData);
        authByAadhaarRequestDto.setScope(scopes);

        transactionDto.setAadharNo("123");
        transactionDto.setAadharTxn("1");
        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setHealthIdNumber("4567");

        aadhaarUserKycDto.setSignature("sign");
        aadhaarResponseDto.setAadhaarAuthOtpDto(new AadhaarAuthOtpDto("success","na","400","1","1","1","7898767121","First_email.com"));
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        String code =aadhaarResponseDto.getAadhaarAuthOtpDto().getCode();
        code=aadhaarResponseDto.getAadhaarAuthOtpDto().getReason();
        code=aadhaarResponseDto.getAadhaarAuthOtpDto().getActionErrorCode();
        code=aadhaarResponseDto.getAadhaarAuthOtpDto().getEmail();
        aadhaarResponseDto.getAadhaarAuthOtpDto().isAuthenticated();

        accountDto.setDayOfBirth("12");
        accountDto.setYearOfBirth("2000");
        accountDto.setMonthOfBirth("12");
        accountDto.setHealthIdNumber("321");
        accountDto.setName("name");
        accountDto.setGender("male");
        accountDto.setKycPhoto("kycPhoto");
        accountDto.setMobile("98765432");
        accountDto.setEmail("email@emil");

        hidPhrAddressDto.setPhrAddress("phrAddress");

        authResponseDto.setAuthResult("");
        authResponseDto.setTxnId("");
        authResponseDto.setMessage("");
        authResponseDto.setAccounts(new ArrayList<AccountResponseDto>());
        AuthResponseDto authResponseDto1=new AuthResponseDto();
        authResponseDto1.setAuthResult(authResponseDto.getAuthResult());
        authResponseDto1.setTxnId(authResponseDto.getTxnId());
        authResponseDto1.setMessage(authResponseDto.getMessage());
        authResponseDto1.setAccounts(authResponseDto.getAccounts());
    }
    @AfterEach
    void tearDown()
    {
        authByAadhaarRequestDto = null;
        accountDto=null;
        otpDto = null;
        authData = null;
        transactionDto = null;
    }

    @Test
    void verifyOtpChildAbhaSuccess(){
        aadhaarResponseDto.setStatus("fail");
        when(Mockito.mock(AadhaarResponseDto.class).isSuccessful()).thenReturn(true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(aadhaarAppService.verifyOtp(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        StepVerifier.create(authByAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpChildAbhaSuccess2(){
        aadhaarResponseDto.setStatus("success");
        when(Mockito.mock(AadhaarResponseDto.class).isSuccessful()).thenReturn(true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(aadhaarAppService.verifyOtp(any())).thenReturn(Mono.just(aadhaarResponseDto));

        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        StepVerifier.create(authByAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpChildAbhaSuccess3(){
        aadhaarResponseDto.setAadhaarAuthOtpDto(new AadhaarAuthOtpDto("success","na","403","1","1","1","7898767121","First_email.com"));
        aadhaarResponseDto.setStatus("status");
        when(Mockito.mock(AadhaarResponseDto.class).isSuccessful()).thenReturn(true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(aadhaarAppService.verifyOtp(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        StepVerifier.create(authByAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpChildAbhaAbhaUnProcessableException(){
        aadhaarResponseDto.setAadhaarAuthOtpDto(new AadhaarAuthOtpDto("success","na","403","1","1","1","7898767121","First_email.com"));
        aadhaarResponseDto.setStatus("success");
        accountDto.setYearOfBirth("2022");
        when(Mockito.mock(AadhaarResponseDto.class).isSuccessful()).thenReturn(true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(aadhaarAppService.verifyOtp(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        StepVerifier.create(authByAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto))
                .expectError(AbhaUnProcessableException.class)
                .verify();
        accountDto.setYearOfBirth("2000");
        accountDto.setHealthIdNumber(transactionDto.getHealthIdNumber());
        StepVerifier.create(authByAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto))
                .expectError(AbhaUnProcessableException.class)
                .verify();

    }
    @Test
    void verifyOtpChildAbhaAadhaarExceptions(){
        aadhaarResponseDto.setAadhaarAuthOtpDto(new AadhaarAuthOtpDto("success","na","1","1","1","1","7898767121","First_email.com"));
        aadhaarResponseDto.setStatus("status");
        when(Mockito.mock(AadhaarResponseDto.class).isSuccessful()).thenReturn(true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(aadhaarAppService.verifyOtp(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        StepVerifier.create(authByAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto))
                .expectError(AadhaarExceptions.class)
                .verify();


    }
}
