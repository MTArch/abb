package in.gov.abdm.abha.enrollment;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.auth.abdm.impl.AuthByAbdmServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthByAbdmServiceTests {

    @InjectMocks
    AuthByAbdmServiceImpl authByAbdmService;
    @Mock
    TransactionService transactionService;
    @Mock
    RedisService redisService;
    private AuthRequestDto authRequestDto;
    private AuthData authData;
    private OtpDto otpDto;
    private TransactionDto transactionDto;
    private RedisOtp redisOtp;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        authRequestDto = new AuthRequestDto();
        otpDto = new OtpDto();
        authData = new AuthData();
        transactionDto = new TransactionDto();
        redisOtp = new RedisOtp();
    }
    @AfterEach
    void tearDown()
    {
        authRequestDto = null;
        otpDto = null;
        authData = null;
        transactionDto = null;
        redisOtp = null;
    }

    @Test
    void verifyOtpViaNotificationDLSuccess() {
        redisOtp.setOtpValue("$argon2id$v=19$m=8192,t=100,p=8$77+9Cw$lwsiQ4xSYcfd9OMS1tNKQfXdCnt8GO0/WiSxCZVYX7TvORWALjmU5zGbCiLIlh43liIZTsDUyJIxkw6HHA5CrvIV3PRS+55ny49iubxcY4t0SH1HNBOMlzh1xPeVt6sH6qd7wnaHCTJaBfyZiPvZYht5vshGXvDom/OP5FspUos");
        when(redisService.getRedisOtp(any()))
                .thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any()))
                .thenReturn(true);

        transactionDto.setTxnId(UUID.fromString("1758856d-480d-4479-86f2-0fa4a3300fc6"));
        transactionDto.setOtp("$argon2id$v=19$m=8192,t=100,p=8$TB8$eQipZfvNNblsk3+subNK5vVqR5muO6vrc5G5L4RlkHU8c1oncmZWACWgjhaOu89GQ4TGkcbLA3Jzll54iHNY2aM7042nGKdIjFVF2OzK89/JFdIKrBK6eE2m006K4SIQWXO9pNzHv7fgmJcndr/YlTU7o4OKx3Qz8EBxZ0cUUGk");
        transactionDto.setCreatedDate(LocalDateTime.now());
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(), any()))
                .thenReturn(Mono.just(transactionDto));

        ArrayList<Scopes> scope = new ArrayList<>();
        scope.add(Scopes.ABHA_ENROL);
        scope.add(Scopes.MOBILE_VERIFY);
        scope.add(Scopes.DL_FLOW);
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.OTP);
        otpDto.setTxnId("1758856d-480d-4479-86f2-0fa4a3300fc6");
        otpDto.setOtpValue("959740");
        authData.setOtp(otpDto);
        authData.setAuthMethods(authMethods);
        authRequestDto.setAuthData(authData);
        authRequestDto.setScope(scope);
        StepVerifier.create(authByAbdmService.verifyOtpViaNotificationDLFlow(authRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
}
