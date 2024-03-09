package in.gov.abdm.abha.enrollment.services.auth;
import com.password4j.Hash;
import com.password4j.HashChecker;
import com.password4j.HashingFunction;
import com.password4j.Password;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.UnauthorizedUserToSendOrVerifyOtpException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.Kyc;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.ErrorResponse;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.auth.abdm.impl.AuthByAbdmServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.idp.IdpAppService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2Util;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthByAbdmServiceTests {

    @InjectMocks
    AuthByAbdmServiceImpl authByAbdmService;
    @Mock
    IdpAppService idpAppService;
    @Mock
    AccountService accountService;
    @Mock
    HidPhrAddressService hidPhrAddressService;
    @Mock
    private AccountAuthMethodService accountAuthMethodService;
    @Mock
    TransactionService transactionService;
    @Mock
    RedisService redisService;
    private AuthRequestDto authRequestDto;
    private AuthData authData;
    private OtpDto otpDto;
    private TransactionDto transactionDto;
    private RedisOtp redisOtp;
    private IdpVerifyOtpResponse idpVerifyOtpResponse;
    private HidPhrAddressDto hidPhrAddressDto;
    ArrayList<AuthMethods> authMethods = new ArrayList<>();
    ArrayList<Scopes> scope;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        authRequestDto = new AuthRequestDto();
        otpDto = new OtpDto();
        authData = new AuthData();
        transactionDto = new TransactionDto();
        idpVerifyOtpResponse=new IdpVerifyOtpResponse();
        hidPhrAddressDto=new HidPhrAddressDto();
        redisOtp = new RedisOtp();
        AccountResponseDto accountResponseDto = AccountResponseDto.builder().build();
        accountResponseDto.setEmail("");
        accountResponseDto.setABHANumber("");
        accountResponseDto.setEnrolmentNumber("");
        accountResponseDto.setKycPhoto("");
        accountResponseDto.setName("");
        accountResponseDto.setPreferredAbhaAddress("");
        accountResponseDto.setYearOfBirth("");
        accountResponseDto.setGender("");
        accountResponseDto.setMobile("");
        AccountResponseDto accountResponseDto2 = AccountResponseDto.builder().build();
        accountResponseDto2.setEmail(accountResponseDto.getEmail());
        accountResponseDto2.setABHANumber(accountResponseDto.getABHANumber());
        accountResponseDto2.setEnrolmentNumber(accountResponseDto.getEnrolmentNumber());
        accountResponseDto2.setKycPhoto(accountResponseDto.getKycPhoto());
        accountResponseDto2.setName(accountResponseDto.getName());
        accountResponseDto2.setPreferredAbhaAddress(accountResponseDto.getPreferredAbhaAddress());
        accountResponseDto2.setYearOfBirth(accountResponseDto.getYearOfBirth());
        accountResponseDto2.setGender(accountResponseDto.getGender());
        accountResponseDto2.setMobile(accountResponseDto.getMobile());

        redisOtp.setReceiver("reciever");
        redisOtp.setTxnId("123");
        redisOtp.setAadhaarTxnId("543");
        redisOtp.setOtpValue("$argon2id$v=19$m=8192,t=100,p=8$77+9Cw$lwsiQ4xSYcfd9OMS1tNKQfXdCnt8GO0/WiSxCZVYX7TvORWALjmU5zGbCiLIlh43liIZTsDUyJIxkw6HHA5CrvIV3PRS+55ny49iubxcY4t0SH1HNBOMlzh1xPeVt6sH6qd7wnaHCTJaBfyZiPvZYht5vshGXvDom/OP5FspUos");
        transactionDto.setTxnId(UUID.fromString("1758856d-480d-4479-86f2-0fa4a3300fc6"));
        transactionDto.setOtp("$argon2id$v=19$m=8192,t=100,p=8$77+9Cw$lwsiQ4xSYcfd9OMS1tNKQfXdCnt8GO0/WiSxCZVYX7TvORWALjmU5zGbCiLIlh43liIZTsDUyJIxkw6HHA5CrvIV3PRS+55ny49iubxcY4t0SH1HNBOMlzh1xPeVt6sH6qd7wnaHCTJaBfyZiPvZYht5vshGXvDom/OP5FspUos");
        transactionDto.setCreatedDate(LocalDateTime.now());
        transactionDto.setHealthIdNumber("123");
        transactionDto.setName("name");
        transactionDto.setAadharTxn("32");
        scope = new ArrayList<>();
        scope.add(Scopes.ABHA_ENROL);
        scope.add(Scopes.MOBILE_VERIFY);
        scope.add(Scopes.DL_FLOW);
        authMethods.add(AuthMethods.OTP);
        otpDto.setTxnId("1758856d-480d-4479-86f2-0fa4a3300fc6");
        otpDto.setOtpValue("$argon2id$v=19$m=8192,t=100,p=8$77+9Cw$lwsiQ4xSYcfd9OMS1tNKQfXdCnt8GO0/WiSxCZVYX7TvORWALjmU5zGbCiLIlh43liIZTsDUyJIxkw6HHA5CrvIV3PRS+55ny49iubxcY4t0SH1HNBOMlzh1xPeVt6sH6qd7wnaHCTJaBfyZiPvZYht5vshGXvDom/OP5FspUos");
        authData.setOtp(otpDto);
        authData.setAuthMethods(authMethods);
        authData=new AuthData(authMethods,otpDto);
        authRequestDto.setAuthData(authData);
        authRequestDto.setScope(scope);


        hidPhrAddressDto.setHealthIdNumber("1234");
        hidPhrAddressDto.setPhrAddress("123");

    }
    @AfterEach
    void tearDown()
    {
        authRequestDto = null;
        otpDto = null;
        authData = null;
        transactionDto = null;
        hidPhrAddressDto=null;
        redisOtp = null;
        idpVerifyOtpResponse=null;
    }

    @Test
    void verifyOtpViaNotificationDLSuccess() {
        when(redisService.getRedisOtp(any()))
                .thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any()))
                .thenReturn(true);
        when(redisService.getReceiverOtpTracker(any()))
                .thenReturn(new ReceiverOtpTracker("reciever",1,1,true));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(), any()))
                .thenReturn(Mono.just(transactionDto));

        StepVerifier.create(authByAbdmService.verifyOtpViaNotificationDLFlow(authRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpViaNotificationSuccess() {
        when(redisService.getRedisOtp(any()))
                .thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any()))
                .thenReturn(true);
        when(redisService.getReceiverOtpTracker(any()))
                .thenReturn(new ReceiverOtpTracker("reciever",1,1,true));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(), any()))
                .thenReturn(Mono.just(transactionDto));

        StepVerifier.create(authByAbdmService.verifyOtpViaNotification(authRequestDto,true))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpSuccess() {
        idpVerifyOtpResponse.setPreferredAbhaAddress("123");
        List<Kyc> listKyc = new ArrayList<>();
        listKyc.add(new Kyc("a","1","a","1998","Male","980239382","email"));
        idpVerifyOtpResponse.setKyc(listKyc);
        when(accountService.getAccountsByHealthIdNumbers(any())).thenReturn(Flux.just(new AccountDto()));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(idpAppService.verifyOtp(any(),any(),any(),any(),any())).thenReturn(Mono.just(idpVerifyOtpResponse));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(transactionService.updateTransactionEntity(any(), any())).thenReturn(Mono.just(transactionDto));
        StepVerifier.create(authByAbdmService.verifyOtp(authRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpSuccessif() {
        idpVerifyOtpResponse.setPreferredAbhaAddress("123");
        List<Kyc> listKyc = new ArrayList<>();
        listKyc.add(new Kyc("a","123","a","1998","Male","980239382","email"));
        idpVerifyOtpResponse.setKyc(listKyc);
        idpVerifyOtpResponse.setError(new ErrorResponse("11","error"));
        when(accountService.getAccountsByHealthIdNumbers(any())).thenReturn(Flux.just(new AccountDto()));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(idpAppService.verifyOtp(any(),any(),any(),any(),any())).thenReturn(Mono.just(idpVerifyOtpResponse));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(transactionService.updateTransactionEntity(any(), any())).thenReturn(Mono.just(transactionDto));
        StepVerifier.create(authByAbdmService.verifyOtp(authRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpSuccessif2() {
        idpVerifyOtpResponse.setPreferredAbhaAddress("123");
        List<Kyc> listKyc = new ArrayList<>();
        listKyc.add(new Kyc("a","13","a","1998","Male","980239382","email"));
        idpVerifyOtpResponse.setKyc(listKyc);
        //idpVerifyOtpResponse.setError(new ErrorResponse("11","error"));
        when(accountService.getAccountsByHealthIdNumbers(any())).thenReturn(Flux.empty());
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(idpAppService.verifyOtp(any(),any(),any(),any(),any())).thenReturn(Mono.just(idpVerifyOtpResponse));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(transactionService.updateTransactionEntity(any(), any())).thenReturn(Mono.just(transactionDto));
        StepVerifier.create(authByAbdmService.verifyOtp(authRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpSuccessif3() {
        idpVerifyOtpResponse.setPreferredAbhaAddress("123");
        List<Kyc> listKyc = new ArrayList<>();
        listKyc.add(new Kyc("a","123","a","1998","Male","980239382","email"));
        idpVerifyOtpResponse.setKyc(listKyc);
        //idpVerifyOtpResponse.setError(new ErrorResponse("11","error"));
        when(accountService.getAccountsByHealthIdNumbers(any())).thenReturn(Flux.empty());
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(idpAppService.verifyOtp(any(),any(),any(),any(),any())).thenReturn(Mono.just(idpVerifyOtpResponse));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(transactionService.updateTransactionEntity(any(), any())).thenReturn(Mono.just(transactionDto));
        StepVerifier.create(authByAbdmService.verifyOtp(authRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpViaNotificationDLSuccessss() {
        redisOtp = new RedisOtp();
        redisOtp.setReceiver("reciever");
        redisOtp.setTxnId("123");
        redisOtp.setAadhaarTxnId("543");
        redisOtp.setOtpValue("$argon2id$v=19$m=8192,t=100,p=8$77+9Cw$lwsiQ4xSYcfd9OMS1tNKQfXdCnt8GO0/WiSxCZVYX7TvORWALjmU5zGbCiLIlh43liIZTsDUyJIxkw6HHA5CrvIV3PRS+55ny49iubxcY4t0SH1HNBOMlzh1xPeVt6sH6qd7wnaHCTJaBfyZiPvZYht5vshGXvDom/OP5FspUos");
        transactionDto.setTxnId(UUID.fromString("1758856d-480d-4479-86f2-0fa4a3300fc6"));
        transactionDto.setOtp("$argon2id$v=19$m=8192,t=100,p=8$77+9Cw$lwsiQ4xSYcfd9OMS1tNKQfXdCnt8GO0/WIZTsDUyJIxkw6HHA5CrvIV3PRS+55ny49iubxcY4t0SH1HNBOMlzh1xPeVt6sH6qd7wnaHCTJaBfyZiPvZYht5vshGXvDom/OP5FspUos");
        transactionDto.setCreatedDate(LocalDateTime.now());
        transactionDto.setHealthIdNumber("123");
        transactionDto.setName("name");
        transactionDto.setAadharTxn("32");
        scope = new ArrayList<>();
        scope.add(Scopes.ABHA_ENROL);
        scope.add(Scopes.MOBILE_VERIFY);
        scope.add(Scopes.DL_FLOW);
        authMethods.add(AuthMethods.OTP);
        otpDto.setTxnId("1758856d-480d-4479-86f2-0fa4a3300fc6");
        otpDto.setOtpValue("$argon2id$v=19$m=8192,t=100,p=8$77+9Cw$lwsiQ4xSYcfd9OMS1tNKQfXdCnt8GO0/WiSxCZVYX7TvORWALjmU5zGbCiLIlh43liIZTsDUyJIxkw6HHA5CrvIV3PRS+55ny49iubxcY4t0SH1HNBOMlzh1xPeVt6sH6qd7wnaHCTJaBfyZiPvZYht5vshGXvDom/OP5FspUos");
        authData.setOtp(otpDto);
        authData.setAuthMethods(authMethods);
        authRequestDto.setAuthData(authData);
        authRequestDto.setScope(scope);

        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(redisService.getReceiverOtpTracker(any())).thenReturn(new ReceiverOtpTracker("reciever",1,1,true));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(), any())).thenReturn(Mono.just(transactionDto));
        StepVerifier.create(authByAbdmService.verifyOtpViaNotification(authRequestDto,true))
                .expectNextCount(1L)
                .verifyComplete();

    }
    @Test
    void verifyOtpViaNotificationError1() {
        Assert.assertThrows(TransactionNotFoundException.class,()->authByAbdmService.verifyOtpViaNotification(authRequestDto,true));
    }
    @Test
    void verifyOtpViaNotificationError2() {
        when(redisService.getRedisOtp(any()))
                .thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any()))
                .thenReturn(false);
        Assert.assertThrows(UnauthorizedUserToSendOrVerifyOtpException.class,()->authByAbdmService.verifyOtpViaNotification(authRequestDto,true));
    }
}
