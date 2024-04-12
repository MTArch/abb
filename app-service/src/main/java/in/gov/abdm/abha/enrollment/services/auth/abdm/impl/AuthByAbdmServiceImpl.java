package in.gov.abdm.abha.enrollment.services.auth.abdm.impl;

import com.password4j.BadParametersException;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaBadRequestException;
import in.gov.abdm.abha.enrollment.exception.application.UnauthorizedUserToSendOrVerifyOtpException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.auth.abdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.idp.IdpAppService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2Util;
import lombok.extern.slf4j.Slf4j;

import in.gov.abdm.error.ABDMError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.UTC_TIMEZONE_ID;
import static in.gov.abdm.abha.enrollment.services.idp.IdpAppService.TIMESTAMP_FORMAT;
import static in.gov.abdm.abha.profile.constants.StringConstants.SENT;
import static java.time.LocalDateTime.now;

@Service
@Slf4j
public class AuthByAbdmServiceImpl implements AuthByAbdmService {

    private static final String OTP_EXPIRED_RESEND_OTP_AND_RETRY = "Please enter a valid OTP. Entered OTP is either expired or incorrect.";
    private static final String OTP_VERIFIED_SUCCESSFULLY = "OTP verified successfully.";
    private static final int OTP_EXPIRE_TIME = 10;
    private static final String AUTHORIZATION = "1233";
    private static final String HIP_REQUEST_ID = "22222";

    private static final String MOBILE_NUMBER_LINKED_SUCCESSFULLY = "Mobile number is now successfully linked to your Account";
    private static final String OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN = "Please enter a valid OTP. Entered OTP is either expired or incorrect.";
    public static final String FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN = "Please enter a valid OTP. Entered OTP is either expired or incorrect.";

    private static final String EMAIL_LINKED_SUCCESSFULLY = "Email address linked successfully";

    private static final String NOTIFICATION_SEND_ON = "Notification send on ";

    private static final String FOR_ABHA_CREATION = "for Abha Creation";

    @Autowired
    IdpAppService idpAppService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    @Autowired
    HidPhrAddressService hidPhrAddressService;

    @Autowired
    private AccountAuthMethodService accountAuthMethodService;
    @Autowired
    NotificationService notificationService;

    @Autowired
    RedisService redisService;

    private RedisOtp redisOtp;

    @Override
    public Mono<AuthResponseDto> verifyOtpViaNotification(AuthRequestDto authByAbdmRequest, boolean isMobile) {
        redisOtp = redisService.getRedisOtp(authByAbdmRequest.getAuthData().getOtp().getTxnId());

        Mono<AuthResponseDto> redisResponse = handleRedisABDMOtpVerification(authByAbdmRequest);
        if (redisResponse != null) {
            return redisResponse;
        }

        return transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId())
                .flatMap(transactionDto -> verifyOtpViaNotification(authByAbdmRequest, transactionDto, isMobile))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    @Override
    public Mono<AuthResponseDto> verifyOtpViaNotificationDLFlow(AuthRequestDto authByAbdmRequest) {
        Mono<AuthResponseDto> redisResponse = handleRedisABDMOtpVerification(authByAbdmRequest);
        if (redisResponse != null) {
            return redisResponse;
        }
        return transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId())
                .flatMap(transactionDto ->
                        verifyOtpViaNotificationDLFlow(authByAbdmRequest.getAuthData().getOtp().getOtpValue(), transactionDto))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AuthResponseDto> handleRedisABDMOtpVerification(AuthRequestDto authByAbdmRequest) {
        redisOtp = redisService.getRedisOtp(authByAbdmRequest.getAuthData().getOtp().getTxnId());
        if (redisOtp == null) {
            throw new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE);
        } else {
            if (!redisService.isMultipleOtpVerificationAllowed(redisOtp.getReceiver())) {
                throw new UnauthorizedUserToSendOrVerifyOtpException();
            }
            if (!Argon2Util.verify(redisOtp.getOtpValue(), authByAbdmRequest.getAuthData().getOtp().getOtpValue())) {
                ReceiverOtpTracker receiverOtpTracker = redisService.getReceiverOtpTracker(redisOtp.getReceiver());
                receiverOtpTracker.setVerifyOtpCount(receiverOtpTracker.getVerifyOtpCount() + 1);
                redisService.saveReceiverOtpTracker(redisOtp.getReceiver(), receiverOtpTracker);
                TransactionDto transactionDto = new TransactionDto();
                transactionDto.setTxnId(UUID.fromString(authByAbdmRequest.getAuthData().getOtp().getTxnId()));
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN);
            }
        }
        return null;
    }

    private Mono<AuthResponseDto> verifyOtpViaNotification(AuthRequestDto authByAbdmRequest, TransactionDto transactionDto,
                                                           boolean isMobile) {
        try {
            if (authByAbdmRequest.getScope().contains(Scopes.fromText(transactionDto.getScope()))) {
                if (GeneralUtils.isOtpExpired(transactionDto.getCreatedDate(), OTP_EXPIRE_TIME)) {
                    return prepareAuthByAdbmResponse(transactionDto, false, OTP_EXPIRED_RESEND_OTP_AND_RETRY);
                } else if (Argon2Util.verify(transactionDto.getOtp(), authByAbdmRequest.getAuthData().getOtp().getOtpValue())) {
                    return accountService.getAccountByHealthIdNumber(transactionDto.getHealthIdNumber())
                            .flatMap(accountDto -> {
                                if (isMobile) {
                                    return updatePhoneNumberInAccountEntity(accountDto, transactionDto);
                                } else {
                                    return updateEmailInAccountEntity(accountDto, transactionDto);
                                }
                            });
                } else {
                    return prepareAuthByAdbmResponse(transactionDto, false, OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN);
                }
            } else {
                throw new AbhaBadRequestException(ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getCode(), ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getMessage());
            }
        } catch (BadParametersException ex) {
            log.error("Error while verifying otp", ex);
            return prepareAuthByAdbmResponse(transactionDto, false, FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN);
        }
    }

    private Mono<? extends AuthResponseDto> updateEmailInAccountEntity(AccountDto accountDto, TransactionDto transactionDto) {

        transactionDto.setEmailVerified(Boolean.TRUE);
        accountDto.setEmail(transactionDto.getEmail());
        accountDto.setEmailVerified(transactionDto.getEmail());
        accountDto.setEmailVerificationDate(now());
        accountDto.setUpdateDate(now());
        redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
        redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());
        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
                .flatMap(transactionDto1 -> accountService.updateAccountByHealthIdNumber(accountDto, accountDto.getHealthIdNumber()))
                .flatMap(accountDto1 -> accountAuthMethodService.addAccountAuthMethods(Collections.singletonList(new AccountAuthMethodsDto(accountDto1.getHealthIdNumber(), AccountAuthMethods.EMAIL_OTP.getValue()))))
                .flatMap(res -> prepareAuthByAdbmResponse(transactionDto, true, EMAIL_LINKED_SUCCESSFULLY));
    }

    private Mono<AuthResponseDto> verifyOtpViaNotificationDLFlow(String otp, TransactionDto transactionDto) {
        try {
            if (GeneralUtils.isOtpExpired(transactionDto.getCreatedDate(), OTP_EXPIRE_TIME)) {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_EXPIRED_RESEND_OTP_AND_RETRY);
            } else if (Argon2Util.verify(transactionDto.getOtp(), otp)) {
                transactionDto.setMobileVerified(true);
                return transactionService.updateTransactionEntity(transactionDto, transactionDto.getTxnId().toString())
                        .flatMap(transactionDtoResponse -> {
                            redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
                            redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());
                            return prepareAuthByAdbmResponse(transactionDto, true, OTP_VERIFIED_SUCCESSFULLY);
                        });
            } else {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN);
            }
        } catch (BadParametersException ex) {
            log.error("Error while validating otp", ex);
            return prepareAuthByAdbmResponse(transactionDto, false, FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN);
        }
    }

    private Mono<AuthResponseDto> updatePhoneNumberInAccountEntity(AccountDto accountDto, TransactionDto
            transactionDto) {

        transactionDto.setMobileVerified(Boolean.TRUE);
        accountDto.setMobile(transactionDto.getMobile());
        accountDto.setUpdateDate(now());
        redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
        redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());

        notificationService.sendABHACreationSMS(accountDto.getMobile(), accountDto.getName(), accountDto.getHealthIdNumber())
                .flatMap(notificationResponseDto -> {
                    if (notificationResponseDto.getStatus().equalsIgnoreCase(SENT))
                        log.info(NOTIFICATION_SEND_ON + accountDto.getMobile() + FOR_ABHA_CREATION);
                    else
                        throw new NotificationGatewayUnavailableException();
                    return Mono.empty();
                }).subscribe();
        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
                .flatMap(transactionDto1 -> accountService.updateAccountByHealthIdNumber(accountDto, accountDto.getHealthIdNumber()))
                .flatMap(accountDto1 -> updateAccountAuthMethodsWithMobileOtp(accountDto1.getHealthIdNumber()))
                .flatMap(res -> prepareAuthByAdbmResponse(transactionDto, true, MOBILE_NUMBER_LINKED_SUCCESSFULLY));
    }

    private Mono<List<AccountAuthMethodsDto>> updateAccountAuthMethodsWithMobileOtp(String abhaNumber) {
        return accountAuthMethodService.addAccountAuthMethods(Collections.singletonList(new AccountAuthMethodsDto(abhaNumber, AccountAuthMethods.MOBILE_OTP.getValue())));
    }

    private Mono<AuthResponseDto> prepareAuthByAdbmResponse(TransactionDto transactionDto, boolean status, String
            message) {

        AccountResponseDto accountResponseDto = null;

        if (status) {
            accountResponseDto = AccountResponseDto.builder()
                    .ABHANumber(transactionDto.getHealthIdNumber())
                    .name(transactionDto.getName())
                    .build();
        }

        return Mono.just(AuthResponseDto.builder()
                .txnId(transactionDto.getTxnId().toString())
                .authResult(status ? StringConstants.SUCCESS : StringConstants.FAILED)
                .message(message)
                .accounts(status && StringUtils.isNoneEmpty(accountResponseDto.getABHANumber()) ? Collections.singletonList(accountResponseDto) : null)
                .build());
    }

    @Override
    public Mono<AuthResponseDto> verifyOtp(AuthRequestDto authByAbdmRequest) {
        Mono<TransactionDto> txnResponseDto = transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId());
        return txnResponseDto.flatMap(res -> verifyMobileOtp(res, authByAbdmRequest))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));

    }

    private Mono<AuthResponseDto> verifyMobileOtp(TransactionDto transactionDto, AuthRequestDto authByAbdmRequest) {
        String xTransactionId = String.valueOf(transactionDto.getTxnId());
        String requestId = transactionDto.getAadharTxn();
        IdpVerifyOtpRequest idpVerifyOtpRequest = new IdpVerifyOtpRequest();
        idpVerifyOtpRequest.setTxnId(xTransactionId);
        idpVerifyOtpRequest.setOtp(authByAbdmRequest.getAuthData().getOtp().getOtpValue());
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE_ID));
        String timestamp = dateFormat.format(new Date());
        return idpAppService.verifyOtp(idpVerifyOtpRequest, AUTHORIZATION, timestamp, HIP_REQUEST_ID, requestId)
                .flatMap(res -> handleIdpMobileOtpResponse(authByAbdmRequest, res, transactionDto));
    }

    private Mono<AuthResponseDto> handleIdpMobileOtpResponse(AuthRequestDto authByAbdmRequest,
                                                             IdpVerifyOtpResponse idpVerifyOtpResponse, TransactionDto transactionDto) {

        if (idpVerifyOtpResponse.getError() != null) {
            return Mono.just(prepareAuthResponse(transactionDto.getTxnId().toString(), StringConstants.FAILED,
                    AbhaConstants.INVALID_OTP, Collections.emptyList()));
        } else {
            List<String> healthIdNumbers = idpVerifyOtpResponse.getKyc().stream()
                    .filter(kyc -> !kyc.getAbhaNumber().equals(transactionDto.getHealthIdNumber().replace("-", "")))
                    .map(kyc -> getHyphenAbhaNumber(kyc.getAbhaNumber())).collect(Collectors.toList());


            if (healthIdNumbers.isEmpty())
                return Mono.just(prepareAuthResponse(transactionDto.getTxnId().toString(), StringConstants.SUCCESS,
                        AbhaConstants.NO_ACCOUNT_FOUND, Collections.emptyList()));

            Flux<AccountDto> accountDtoFlux = accountService.getAccountsByHealthIdNumbers(healthIdNumbers);

            return accountDtoFlux.collectList().flatMap(Mono::just).flatMap(accountDtoList -> {

                Flux<HidPhrAddressDto> fluxPhrAaddress = hidPhrAddressService
                        .getHidPhrAddressByHealthIdNumbersAndPreferredIn(healthIdNumbers,
                                new ArrayList<>(Collections.singleton(1)));

                return fluxPhrAaddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> handleAccountListResponse(authByAbdmRequest, accountDtoList, phrAddressList, healthIdNumbers,
                        transactionDto)).switchIfEmpty(Mono.defer(() -> handleAccountListResponse(authByAbdmRequest, accountDtoList, Collections.emptyList(),
                        healthIdNumbers, transactionDto)));

            }).switchIfEmpty(Mono.defer(() -> Mono.just(prepareAuthResponse(transactionDto.getTxnId().toString(), StringConstants.SUCCESS,
                    AbhaConstants.NO_ACCOUNT_FOUND, Collections.emptyList()))));
        }
    }

    private Mono<AuthResponseDto> handleAccountListResponse(AuthRequestDto
                                                                    authByAbdmRequest, List<AccountDto> accountDtoList, List<HidPhrAddressDto> phrAddressList, List<String> healthIdNumbers, TransactionDto
                                                                    transactionDto) {
        if (accountDtoList != null && !accountDtoList.isEmpty()) {
            transactionDto.setTxnResponse(healthIdNumbers.stream().collect(Collectors.joining(",")));

            List<AccountResponseDto> accountResponseDtoList = prepareAccountResponseDtoList(accountDtoList,
                    phrAddressList);

            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
                    .flatMap(response -> accountResponse(authByAbdmRequest, accountResponseDtoList))
                    .switchIfEmpty(Mono.error(new AbhaDBGatewayUnavailableException()));
        }
        return Mono.empty();
    }
    @SuppressWarnings("java:S3776")
    private List<AccountResponseDto> prepareAccountResponseDtoList(List<AccountDto> accountDtoList,
                                                                   List<HidPhrAddressDto> phrAddressList) {

        return accountDtoList.stream().map(accountDto -> {
            Optional<String> reducedValue = phrAddressList.stream()
                    .filter(hidPhrAddDto -> hidPhrAddDto.getHealthIdNumber().equals(accountDto.getHealthIdNumber()))
                    .map(HidPhrAddressDto::getPhrAddress).reduce((first, next) -> first);

            return MapperUtils.mapAccountDtoToAccountResponse(accountDto,
                    reducedValue.isPresent() ? reducedValue.get() : StringConstants.EMPTY);
        }).collect(Collectors.toList());
    }

    private Mono<AuthResponseDto> accountResponse(AuthRequestDto authByAbdmRequest, List<AccountResponseDto> accountDtoList) {
        if (accountDtoList != null && !accountDtoList.isEmpty()) {
            return Mono.just(AuthResponseDto.builder().txnId(authByAbdmRequest.getAuthData().getOtp().getTxnId())
                    .authResult(StringConstants.SUCCESS)
                    .accounts(accountDtoList)
                    .build());
        }
        return Mono.empty();
    }

    private AuthResponseDto prepareAuthResponse(String transactionId, String authResult, String message,
                                                List<AccountResponseDto> accounts) {
        return AuthResponseDto.builder()
                .txnId(transactionId)
                .authResult(authResult)
                .message(message)
                .accounts(accounts)
                .build();
    }

    public String getHyphenAbhaNumber(String abhaNumber) {
        return abhaNumber.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "$1-$2-$3-");
    }
}
