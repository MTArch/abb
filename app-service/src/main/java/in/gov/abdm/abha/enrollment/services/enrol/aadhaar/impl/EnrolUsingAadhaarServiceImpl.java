package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.KycAuthType;
import in.gov.abdm.abha.enrollment.enums.TransactionStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnAuthorizedException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.exception.application.UnauthorizedUserToSendOrVerifyOtpException;
import in.gov.abdm.abha.enrollment.exception.hidbenefit.BenefitNotFoundException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyFaceAuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.notification.NotificationType;
import in.gov.abdm.abha.enrollment.model.notification.SendNotificationRequestDto;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.notification.TemplatesHelper;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;
import static in.gov.abdm.abha.enrollment.model.notification.NotificationType.EMAIL;
import static in.gov.abdm.abha.enrollment.model.notification.NotificationType.SMS;

@Service
@Slf4j
@SuppressWarnings("java:S3776")
public class EnrolUsingAadhaarServiceImpl implements EnrolUsingAadhaarService {

    private static final String NOTIFICATION_SENT_ON_ACCOUNT_CREATION = "Notification sent successfully on Account Creation";
    private static final String ON_MOBILE_NUMBER = "on Mobile Number:";
    private static final String FOR_HEALTH_ID_NUMBER = "for HealthIdNumber:";

    private static final String INTEGRATED_PROGRAMS_LOADED_FROM_REDIS = "Integrated Programs loaded from Redis ::";
    private static final String FAILED_TO_LOAD_INTEGRATED_PROGRAMS = "Failed to load Integrated Programs";

    @Autowired
    AccountService accountService;
    @Autowired
    HidPhrAddressService hidPhrAddressService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    RSAUtil rsaUtil;
    @Autowired
    private AccountAuthMethodService accountAuthMethodService;
    @Autowired
    RedisService redisService;
    @Autowired
    AbhaAddressGenerator abhaAddressGenerator;

    @Autowired
    NotificationService notificationService;

    @Autowired
    DeDuplicationService deDuplicationService;

    private RedisOtp redisOtp;

    @Autowired
    AadhaarAppService aadhaarAppService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    LgdUtility lgdUtility;
    @Autowired
    TemplatesHelper templatesHelper;

    @Value(PropertyConstants.ENROLLMENT_MAX_MOBILE_LINKING_COUNT)
    private int maxMobileLinkingCount;
    @Value(PropertyConstants.ENROLLMENT_IS_TRANSACTION)
    private boolean isTransactionManagementEnable;

    @Override
    public Mono<EnrolByAadhaarResponseDto> verifyOtp(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        redisOtp = redisService.getRedisOtp(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId());
        if (redisOtp == null) {
            throw new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE);
        } else {
            if (!redisService.isMultipleOtpVerificationAllowed(redisOtp.getReceiver())) {
                throw new UnauthorizedUserToSendOrVerifyOtpException();
            }
            return accountService.getMobileLinkedAccountCount(enrolByAadhaarRequestDto.getAuthData().getOtp().getMobile())
                    .flatMap(mobileLinkedAccountCount -> {
                        if (mobileLinkedAccountCount >= maxMobileLinkingCount) {
                            throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS.getCode(), MessageFormat.format(MOBILE_ALREADY_LINKED_TO_MAX_ACCOUNTS, maxMobileLinkingCount));
                        } else {
                            Mono<AadhaarResponseDto> aadhaarResponseDtoMono =
                                    aadhaarAppService.verifyOtp(AadhaarVerifyOtpRequestDto.builder()
                                            .aadhaarNumber(rsaUtil.encrypt(redisOtp.getReceiver()))
                                            .aadhaarTransactionId(redisOtp.getAadhaarTxnId())
                                            .otp(enrolByAadhaarRequestDto.getAuthData().getOtp().getOtpValue())
                                            .build());

                            return aadhaarResponseDtoMono.flatMap(aadhaarResponseDto -> handleAadhaarOtpResponse(enrolByAadhaarRequestDto, aadhaarResponseDto, requestHeaders));
                        }
                    });
        }
    }

    @Override
    public Mono<String> requestNotification(SendNotificationRequestDto sendNotificationRequestDto, RequestHeaders requestHeaders) {
        String abhaNumber = sendNotificationRequestDto.getAbhaNumber();

        return accountService.getAccountByHealthIdNumber(abhaNumber)
                .flatMap(accountDtoResponse ->
                {
                    if (accountDtoResponse.getStatus().equalsIgnoreCase(AccountStatus.ACTIVE.getValue())) {
                        if (sendNotificationRequestDto.getType().equalsIgnoreCase(CREATION) && sendNotificationRequestDto.getNotificationType().equals(List.of(SMS)) && null != accountDtoResponse.getMobile()) {
                            notificationService.sendABHACreationSMS(accountDtoResponse.getMobile(), accountDtoResponse.getName(), accountDtoResponse.getHealthIdNumber()).subscribe();
                            return Mono.empty();
                        } else if (sendNotificationRequestDto.getType().equalsIgnoreCase(CREATION) && sendNotificationRequestDto.getNotificationType().equals(List.of(EMAIL)) && null != accountDtoResponse.getEmail()) {
                            return templatesHelper.prepareSMSMessage(ABHA_CREATED_TEMPLATE_ID, accountDtoResponse.getName(), abhaNumber).flatMap(
                                    message ->
                                    {
                                        notificationService.sendEmailOtp(accountDtoResponse.getEmail(), EMAIL_ACCOUNT_CREATION_SUBJECT, message).subscribe();
                                        return Mono.empty();
                                    }
                            );

                        } else if (sendNotificationRequestDto.getType().equalsIgnoreCase(CREATION) && new HashSet<>(sendNotificationRequestDto.getNotificationType()).containsAll(List.of(SMS, EMAIL))) {
                            return templatesHelper.prepareSMSMessage(ABHA_CREATED_TEMPLATE_ID, accountDtoResponse.getName(), abhaNumber).flatMap(
                                    message ->
                                    {
                                        if (null != accountDtoResponse.getEmail()) {
                                            notificationService.sendSmsAndEmailOtp(accountDtoResponse.getEmail(), accountDtoResponse.getMobile(), EMAIL_ACCOUNT_CREATION_SUBJECT, message).subscribe();
                                        } else {
                                            notificationService.sendABHACreationSMS(accountDtoResponse.getMobile(), accountDtoResponse.getName(), accountDtoResponse.getHealthIdNumber()).subscribe();
                                        }
                                        return Mono.empty();
                                    }
                            );

                        }
                    }
                    return Mono.empty();
                });


    }

    private Mono<EnrolByAadhaarResponseDto> handleAadhaarOtpResponse(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, RequestHeaders requestHeaders) {

        handleAadhaarExceptions(aadhaarResponseDto);

        return transactionService.findTransactionDetailsFromDB(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId()).flatMap(transactionDto -> {
            transactionService.mapTransactionWithEkyc(transactionDto, aadhaarResponseDto.getAadhaarUserKycDto(), KycAuthType.OTP.getValue());
            return accountService.findByXmlUid(aadhaarResponseDto.getAadhaarUserKycDto().getSignature())
                    .flatMap(existingAccount -> {
                        if (existingAccount.getStatus().equals(AccountStatus.DELETED.getValue())) {
                            return createNewAccount(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto, requestHeaders);
                        } else if (existingAccount.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                            return existingAccount(transactionDto, aadhaarResponseDto, existingAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED);
                        } else if (existingAccount.getStatus().equals(AccountStatus.ACTIVE.getValue())) {
                            return checkKycAndUpdate(aadhaarResponseDto, existingAccount)
                                    .flatMap(updatedAccountDto -> existingAccount(transactionDto, aadhaarResponseDto, updatedAccountDto, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST));
                        } else {
                            return existingAccount(transactionDto, aadhaarResponseDto, existingAccount, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST);
                        }
                    })
                    .switchIfEmpty(Mono.defer(() -> createNewAccount(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto, requestHeaders)));
        });
    }

    private Mono<AccountDto> checkKycAndUpdate(AadhaarResponseDto aadhaarResponseDto, AccountDto existingAccount) {
        return lgdUtility.getLgdData(aadhaarResponseDto.getAadhaarUserKycDto().getPincode(), aadhaarResponseDto.getAadhaarUserKycDto().getState())
                .flatMap(lgdDistrictResponses -> {
                    accountService.mapAccountWithEkyc(aadhaarResponseDto, existingAccount, lgdDistrictResponses);
                    return accountService.updateAccountByHealthIdNumber(existingAccount, existingAccount.getHealthIdNumber());
                });
    }

    private Mono<EnrolByAadhaarResponseDto> existingAccount(TransactionDto transactionDto, AadhaarResponseDto aadhaarResponseDto, AccountDto accountDto, boolean generateToken, String responseMessage) {

        return transactionService.findTransactionDetailsFromDB(String.valueOf(transactionDto.getTxnId()))
                .flatMap(transactionDtoResponse ->
                {
                    transactionDtoResponse.setHealthIdNumber(accountDto.getHealthIdNumber());
                    if (!generateToken)
                        transactionDtoResponse.setStatus(AccountStatus.DEACTIVATED.getValue());
                    return transactionService.updateTransactionEntity(transactionDtoResponse, String.valueOf(transactionDto.getTxnId()))
                            .flatMap(res -> {
                                ABHAProfileDto abhaProfileDto = MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), accountDto);
                                Flux<String> fluxPhrAddress = hidPhrAddressService
                                        .getHidPhrAddressByHealthIdNumbersAndPreferredIn(Arrays.asList(accountDto.getHealthIdNumber()), Arrays.asList(1, 0)).map(HidPhrAddressDto::getPhrAddress);

                                return fluxPhrAddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> {
                                    abhaProfileDto.setPhrAddress(phrAddressList);
                                    redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
                                    redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());

                                    EnrolByAadhaarResponseDto enrolByAadhaarResponseDto = EnrolByAadhaarResponseDto.builder()
                                            .txnId(transactionDto.getTxnId().toString())
                                            .abhaProfileDto(abhaProfileDto)
                                            .message(responseMessage)
                                            .isNew(false)
                                            .build();

                                    if (generateToken) {
                                        ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                                                .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDto))
                                                .expiresIn(jwtUtil.jwtTokenExpiryTime())
                                                .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                                                .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                                                .build();

                                        enrolByAadhaarResponseDto.setResponseTokensDto(responseTokensDto);
                                    }

                                    // Final response for existing user
                                    return Mono.just(enrolByAadhaarResponseDto);
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto, RequestHeaders requestHeaders) {
        Mono<AccountDto> newAccountDto = lgdUtility.getLgdData(transactionDto.getPincode(), transactionDto.getStateName())
                .flatMap(lgdDistrictResponse -> accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto, lgdDistrictResponse));
        return newAccountDto.flatMap(accountDto -> {
            accountDto.setFacilityId(requestHeaders.getFTokenClaims() != null && requestHeaders.getFTokenClaims().get(SUB) != null ? requestHeaders.getFTokenClaims().get(SUB).toString() : null);
            return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
                    .flatMap(duplicateAccount -> existingAccount(transactionDto, aadhaarResponseDto, duplicateAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST)).switchIfEmpty(Mono.defer(() -> {
                        int age = Common.calculateYearDifference(accountDto.getYearOfBirth(), accountDto.getMonthOfBirth(), accountDto.getDayOfBirth());
                        if (age >= 18) {
                            accountDto.setType(AbhaType.STANDARD);
                        } else {
                            accountDto.setType(AbhaType.CHILD);
                        }
                        accountDto.setStatus(AccountStatus.ACTIVE.toString());
                        String newAbhaNumber = AbhaNumberGenerator.generateAbhaNumber();
                        transactionDto.setHealthIdNumber(newAbhaNumber);
                        accountDto.setHealthIdNumber(newAbhaNumber);
                        ABHAProfileDto abhaProfileDto = MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), accountDto);
                        String defaultAbhaAddress = abhaAddressGenerator.generateDefaultAbhaAddress(newAbhaNumber);
                        accountDto.setHealthId(defaultAbhaAddress);
                        abhaProfileDto.setPhrAddress(new ArrayList<>(Collections.singleton(defaultAbhaAddress)));
                        abhaProfileDto.setStateCode(accountDto.getStateCode());
                        abhaProfileDto.setDistrictCode(accountDto.getDistrictCode());

                        String userEnteredPhoneNumber = enrolByAadhaarRequestDto.getAuthData().getOtp().getMobile();
                        if (Common.isPhoneNumberMatching(userEnteredPhoneNumber, transactionDto.getMobile())) {
                            return aadhaarAppService.verifyDemographicDetails(prepareVerifyDemographicRequest(accountDto, transactionDto, enrolByAadhaarRequestDto))
                                    .flatMap(verifyDemographicResponse -> {
                                        if (verifyDemographicResponse.isVerified()) {
                                            accountDto.setMobile(userEnteredPhoneNumber);
                                            abhaProfileDto.setMobile(userEnteredPhoneNumber);
                                        }
                                        // update transaction table and create account in account table
                                        // account status is active
                                        if (!isTransactionManagementEnable) {
                                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                                    .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                                    .flatMap(response -> handleCreateAccountResponse(response, transactionDto, abhaProfileDto, requestHeaders));
                                        } else {
                                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                                    .flatMap(transactionDtoResponse -> accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                                    .flatMap(response -> callProcedureToCreateAccount(response, transactionDto, abhaProfileDto, requestHeaders));
                                        }

                                    });
                        } else {
                            // update transaction table and create account in account table
                            // account status is active
                            if (!isTransactionManagementEnable) {
                                return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                        .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                        .flatMap(response -> handleCreateAccountResponse(response, transactionDto, abhaProfileDto, requestHeaders));
                            } else {
                                return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                        .flatMap(transactionDtoResponse -> accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                        .flatMap(response -> callProcedureToCreateAccount(response, transactionDto, abhaProfileDto, requestHeaders));
                            }
                        }

                    }));
        });
    }


    private Mono<EnrolByAadhaarResponseDto> handleCreateAccountResponse(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto, RequestHeaders requestHeaders) {

        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDtoResponse, abhaProfileDto);

        return hidPhrAddressService.createHidPhrAddressEntity(hidPhrAddressDto).flatMap(response -> {
            if (!accountDtoResponse.getHealthIdNumber().isEmpty()) {

                List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_OTP.getValue()));
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.DEMOGRAPHICS.getValue()));
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_BIO.getValue()));
                if (accountDtoResponse.getMobile() != null) {
                    abhaProfileDto.setMobile(accountDtoResponse.getMobile());
                    accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
                }
                return accountAuthMethodService.addAccountAuthMethods(accountAuthMethodsDtos)
                        .flatMap(res -> {
                            if (!res.isEmpty()) {
                                redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
                                redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());

                                return addAccountAuthMethods(transactionDto, accountDtoResponse, abhaProfileDto, requestHeaders);

                            } else {
                                throw new AbhaDBGatewayUnavailableException();
                            }
                        });
            } else {
                throw new AbhaDBGatewayUnavailableException();
            }
        });
    }

    private Mono<EnrolByAadhaarResponseDto> addAccountAuthMethods(TransactionDto transactionDto, AccountDto accountDtoResponse, ABHAProfileDto abhaProfileDto, RequestHeaders requestHeaders) {
        if (accountDtoResponse.getMobile() != null && !accountDtoResponse.getMobile().isBlank() && !DEFAULT_CLIENT_ID.equalsIgnoreCase(requestHeaders.getClientId())) {


            return notificationService.sendABHACreationSMS(accountDtoResponse.getMobile(), accountDtoResponse.getName(), accountDtoResponse.getHealthIdNumber())
                    .flatMap(notificationResponseDto -> {
                        if (notificationResponseDto.getStatus().equals(SENT)) {
                            log.info(NOTIFICATION_SENT_ON_ACCOUNT_CREATION + ON_MOBILE_NUMBER + accountDtoResponse.getMobile() + FOR_HEALTH_ID_NUMBER + accountDtoResponse.getHealthIdNumber());
                            ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                                    .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDtoResponse))
                                    .expiresIn(jwtUtil.jwtTokenExpiryTime())
                                    .refreshToken(jwtUtil.generateRefreshToken(accountDtoResponse.getHealthIdNumber()))
                                    .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                                    .build();
                            // final create new account response
                            return Mono.just(EnrolByAadhaarResponseDto.builder()
                                    .txnId(transactionDto.getTxnId().toString())
                                    .abhaProfileDto(abhaProfileDto).responseTokensDto(responseTokensDto)
                                    .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                                    .isNew(true)
                                    .build());
                        } else {
                            throw new NotificationGatewayUnavailableException();
                        }
                    });
        } else {
            ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                    .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDtoResponse))
                    .expiresIn(jwtUtil.jwtTokenExpiryTime())
                    .refreshToken(jwtUtil.generateRefreshToken(accountDtoResponse.getHealthIdNumber()))
                    .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                    .build();
            // final create new account response
            return Mono.just(EnrolByAadhaarResponseDto.builder()
                    .txnId(transactionDto.getTxnId().toString())
                    .abhaProfileDto(abhaProfileDto).responseTokensDto(responseTokensDto)
                    .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                    .isNew(true)
                    .build());
        }

    }

    private void handleAadhaarExceptions(AadhaarResponseDto aadhaarResponseDto) {
        if (!aadhaarResponseDto.isSuccessful()) {
            if (aadhaarResponseDto.getAadhaarAuthOtpDto() != null) {
                if (redisService.isReceiverOtpTrackerAvailable(redisOtp.getReceiver())) {
                    ReceiverOtpTracker receiverOtpTracker = redisService.getReceiverOtpTracker(redisOtp.getReceiver());
                    receiverOtpTracker.setVerifyOtpCount(receiverOtpTracker.getVerifyOtpCount() + 1);
                    redisService.saveReceiverOtpTracker(redisOtp.getReceiver(), receiverOtpTracker);
                }
                throw new AadhaarExceptions(aadhaarResponseDto.getAadhaarAuthOtpDto().getErrorCode());
            } else {
                throw new AadhaarExceptions(aadhaarResponseDto.getErrorCode());
            }
        }
    }

    private VerifyDemographicRequest prepareVerifyDemographicRequest(AccountDto accountDto, TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        return VerifyDemographicRequest.builder()
                .aadhaarNumber(rsaUtil.decrypt(transactionDto.getAadharNo()))
                .name(accountDto.getName())
                .phone(enrolByAadhaarRequestDto.getAuthData().getOtp().getMobile())
                .build();
    }

    @Override
    public Mono<EnrolByAadhaarResponseDto> faceAuth(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        if (enrolByAadhaarRequestDto.getAuthData().getFace().getMobile() == null || enrolByAadhaarRequestDto.getAuthData().getFace().getMobile().isBlank()) {
            return verifyAadhaarFace(enrolByAadhaarRequestDto, requestHeaders);
        } else {
            return accountService.getMobileLinkedAccountCount(enrolByAadhaarRequestDto.getAuthData().getFace().getMobile())
                    .flatMap(mobileLinkedAccountCount -> {
                        if (mobileLinkedAccountCount >= maxMobileLinkingCount) {
                            throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS);
                        } else {
                            return verifyAadhaarFace(enrolByAadhaarRequestDto, requestHeaders);
                        }
                    });
        }

    }

    private Mono<EnrolByAadhaarResponseDto> verifyAadhaarFace(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        Mono<AadhaarResponseDto> aadhaarResponseDtoMono = aadhaarAppService.faceAuth(AadhaarVerifyFaceAuthRequestDto.builder()
                .aadhaarNumber(rsaUtil.decrypt(enrolByAadhaarRequestDto.getAuthData().getFace().getAadhaar()))
                .faceAuthPid(enrolByAadhaarRequestDto.getAuthData().getFace().getRdPidData())
                .build());
        return aadhaarResponseDtoMono.flatMap(aadhaarResponseDto -> handleAadhaarFaceResponse(enrolByAadhaarRequestDto, aadhaarResponseDto, requestHeaders));
    }

    private Mono<EnrolByAadhaarResponseDto> handleAadhaarFaceResponse(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, RequestHeaders requestHeaders) {

        handleAadhaarExceptions(aadhaarResponseDto);
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setStatus(TransactionStatus.ACTIVE.toString());
        transactionDto.setAadharNo(enrolByAadhaarRequestDto.getAuthData().getFace().getAadhaar());
        transactionDto.setClientIp(Common.getIpAddress());
        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setKycPhoto(Base64.getEncoder().encodeToString(new byte[1]));
        transactionDto.setMobile(aadhaarResponseDto.getAadhaarUserKycDto().getPhone());
        transactionDto.setAadharTxn(aadhaarResponseDto.getAadhaarUserKycDto().getUidiaTxn());
        transactionDto.setCreatedDate(LocalDateTime.now());

        return transactionService.createTransactionEntity(transactionDto).flatMap(transaction -> {
            transactionService.mapTransactionWithEkyc(transaction, aadhaarResponseDto.getAadhaarUserKycDto(), KycAuthType.OTP.getValue());
            return accountService.findByXmlUid(aadhaarResponseDto.getAadhaarUserKycDto().getSignature()).flatMap(existingAccount -> {
                if (existingAccount.getStatus().equals(AccountStatus.DELETED.getValue())) {
                    return createNewAccountUsingFAceAuth(enrolByAadhaarRequestDto, aadhaarResponseDto, transaction, requestHeaders);
                } else if (existingAccount.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                    return existingAccountFaceAuth(transaction, aadhaarResponseDto, existingAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED);
                } else {
                    return existingAccountFaceAuth(transaction, aadhaarResponseDto, existingAccount, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST);
                }
            }).switchIfEmpty(Mono.defer(() -> createNewAccountUsingFAceAuth(enrolByAadhaarRequestDto, aadhaarResponseDto, transaction, requestHeaders)));
        });
    }


    private Mono<EnrolByAadhaarResponseDto> createNewAccountUsingFAceAuth(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto, RequestHeaders requestHeaders) {
        Mono<AccountDto> newAccountDto = lgdUtility.getLgdData(transactionDto.getPincode(), transactionDto.getStateName())
                .flatMap(lgdDistrictResponse -> accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto, lgdDistrictResponse));
        return newAccountDto.flatMap(accountDto -> {
            accountDto.setFacilityId(requestHeaders.getFTokenClaims() != null && requestHeaders.getFTokenClaims().get(SUB) != null ? requestHeaders.getFTokenClaims().get(SUB).toString() : null);
            return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
                    .flatMap(duplicateAccount -> existingAccountFaceAuth(transactionDto, aadhaarResponseDto, duplicateAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST)).switchIfEmpty(Mono.defer(() -> {
                        int age = Common.calculateYearDifference(accountDto.getYearOfBirth(), accountDto.getMonthOfBirth(), accountDto.getDayOfBirth());
                        if (age >= 18) {
                            accountDto.setType(AbhaType.STANDARD);
                        } else {
                            accountDto.setType(AbhaType.CHILD);
                        }

                        accountDto.setStatus(AccountStatus.ACTIVE.toString());
                        String newAbhaNumber = AbhaNumberGenerator.generateAbhaNumber();
                        transactionDto.setHealthIdNumber(newAbhaNumber);
                        accountDto.setHealthIdNumber(newAbhaNumber);
                        accountDto.setMobile(transactionDto.getMobile());
                        ABHAProfileDto abhaProfileDto = MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), accountDto);
                        String defaultAbhaAddress = abhaAddressGenerator.generateDefaultAbhaAddress(newAbhaNumber);
                        accountDto.setHealthId(defaultAbhaAddress);
                        abhaProfileDto.setPhrAddress(new ArrayList<>(Collections.singleton(defaultAbhaAddress)));
                        abhaProfileDto.setStateCode(accountDto.getStateCode());
                        abhaProfileDto.setDistrictCode(accountDto.getDistrictCode());

                        String userEnteredPhoneNumber = enrolByAadhaarRequestDto.getAuthData().getFace().getMobile();
                        if (userEnteredPhoneNumber != null && !userEnteredPhoneNumber.isBlank() && Common.isPhoneNumberMatching(userEnteredPhoneNumber, transactionDto.getMobile())) {
                            return aadhaarAppService.verifyDemographicDetails(prepareVerifyDemographicRequest(accountDto, transactionDto, enrolByAadhaarRequestDto))
                                    .flatMap(verifyDemographicResponse -> {
                                        if (verifyDemographicResponse.isVerified()) {
                                            accountDto.setMobile(userEnteredPhoneNumber);
                                            abhaProfileDto.setMobile(userEnteredPhoneNumber);
                                        }
                                        // update transaction table and create account in account table
                                        // account status is active
                                        if (!isTransactionManagementEnable) {
                                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                                    .flatMap(transactionDtoResponse -> accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                                    .flatMap(response -> callProcedureToCreateAccount(response, transactionDto, abhaProfileDto, requestHeaders));
                                        } else {
                                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                                    .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                                    .flatMap(response -> handleCreateAccountResponseUsingFaceAuth(response, transactionDto, abhaProfileDto, requestHeaders));
                                        }
                                    });
                        } else {
                            // update transaction table and create account in account table
                            // account status is active
                            if (!isTransactionManagementEnable) {
                                return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                        .flatMap(transactionDtoResponse -> accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                        .flatMap(response -> callProcedureToCreateAccount(response, transactionDto, abhaProfileDto, requestHeaders));
                            } else {
                                return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                        .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                        .flatMap(response -> handleCreateAccountResponseUsingFaceAuth(response, transactionDto, abhaProfileDto, requestHeaders));
                            }
                        }
                    }));
        });
    }

    private Mono<EnrolByAadhaarResponseDto> handleCreateAccountResponseUsingFaceAuth(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto, RequestHeaders requestHeaders) {

        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDtoResponse, abhaProfileDto);

        return hidPhrAddressService.createHidPhrAddressEntity(hidPhrAddressDto).flatMap(response -> {
            if (!accountDtoResponse.getHealthIdNumber().isEmpty()) {

                List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_OTP.getValue()));
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.DEMOGRAPHICS.getValue()));
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_BIO.getValue()));
                if (accountDtoResponse.getMobile() != null) {
                    accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
                }
                return accountAuthMethodService.addAccountAuthMethods(accountAuthMethodsDtos)
                        .flatMap(res -> {
                            if (!res.isEmpty()) {
                                return addAccountAuthMethods(transactionDto, accountDtoResponse, abhaProfileDto, requestHeaders);
                            } else {
                                throw new AbhaDBGatewayUnavailableException();
                            }
                        });
            } else {
                throw new AbhaDBGatewayUnavailableException();
            }
        });
    }

    private Mono<EnrolByAadhaarResponseDto> existingAccountFaceAuth(TransactionDto transactionDto, AadhaarResponseDto aadhaarResponseDto, AccountDto accountDto, boolean generateToken, String responseMessage) {

        return transactionService.findTransactionDetailsFromDB(String.valueOf(transactionDto.getTxnId()))
                .flatMap(transactionDtoResponse ->
                {
                    transactionDtoResponse.setHealthIdNumber(accountDto.getHealthIdNumber());
                    return transactionService.updateTransactionEntity(transactionDtoResponse, String.valueOf(transactionDto.getTxnId()))
                            .flatMap(res -> {
                                ABHAProfileDto abhaProfileDto = MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), accountDto);
                                Flux<String> fluxPhrAddress = hidPhrAddressService
                                        .getHidPhrAddressByHealthIdNumbersAndPreferredIn(Arrays.asList(accountDto.getHealthIdNumber()), Arrays.asList(1, 0)).map(HidPhrAddressDto::getPhrAddress);

                                return fluxPhrAddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> {
                                    abhaProfileDto.setPhrAddress(phrAddressList);
                                    EnrolByAadhaarResponseDto enrolByAadhaarResponseDto = EnrolByAadhaarResponseDto.builder()
                                            .txnId(transactionDto.getTxnId().toString())
                                            .abhaProfileDto(abhaProfileDto)
                                            .message(responseMessage)
                                            .isNew(false)
                                            .build();

                                    if (generateToken) {
                                        ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                                                .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDto))
                                                .expiresIn(jwtUtil.jwtTokenExpiryTime())
                                                .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                                                .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                                                .build();
                                        enrolByAadhaarResponseDto.setResponseTokensDto(responseTokensDto);
                                    }
                                    // Final response for existing user
                                    return Mono.just(enrolByAadhaarResponseDto);
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    @Override
    public Mono<Boolean> validateHeaders(RequestHeaders requestHeaders, List<AuthMethods> authMethods, String fToken) {
        if (Boolean.FALSE.equals(isAuthorized(requestHeaders, authMethods, fToken))) {
            throw new BenefitNotFoundException(BENEFIT_NAME_OR_F_TOKEN_REQUIRED);
        }
        if (Boolean.FALSE.equals(isValidFacility(requestHeaders, authMethods, fToken))) {
            throw new AbhaUnAuthorizedException(ABDMError.INVALID_F_TOKEN.getCode(), ABDMError.INVALID_F_TOKEN.getMessage());
        }
        if (Boolean.FALSE.equals(isValidBenefitRole(requestHeaders, authMethods))) {
            throw new BenefitNotFoundException(INVALID_BENEFIT_ROLE);
        }
        return validateBenefitProgram(requestHeaders, authMethods);
    }

    private Boolean isAuthorized(RequestHeaders requestHeaders, List<AuthMethods> authMethods, String fToken) {
        return !(((authMethods != null && !authMethods.isEmpty()
                && (authMethods.contains(AuthMethods.DEMO) || authMethods.contains(AuthMethods.BIO) || authMethods.contains(AuthMethods.IRIS)))
                && ((requestHeaders.getBenefitName() == null || requestHeaders.getBenefitName().isEmpty())
                && (fToken == null || fToken.isEmpty())))
                || ((authMethods != null && !authMethods.isEmpty()
                && (authMethods.contains(AuthMethods.OTP) || authMethods.contains(AuthMethods.DEMO) || authMethods.contains(AuthMethods.BIO) || authMethods.contains(AuthMethods.IRIS) || authMethods.contains(AuthMethods.FACE)))
                && ((requestHeaders.getBenefitName() != null && !requestHeaders.getBenefitName().isEmpty())
                && (fToken != null && !fToken.isEmpty()))));
    }

    private Mono<Boolean> validateBenefitProgram(RequestHeaders requestHeaders, List<AuthMethods> authMethods) {
        if (authMethods != null && (authMethods.contains(AuthMethods.OTP) || authMethods.contains(AuthMethods.BIO)
                || authMethods.contains(AuthMethods.DEMO))) {
            if (!authMethods.contains(AuthMethods.OTP) && requestHeaders.getRoleList() != null && requestHeaders.getRoleList().contains(INTEGRATED_PROGRAM_ROLE)
                    && (requestHeaders.getBenefitName() == null || requestHeaders.getBenefitName().isEmpty())) {
                throw new BenefitNotFoundException(INVALID_BENEFIT_NAME);
            }
            if (requestHeaders.getBenefitName() != null && requestHeaders.getRoleList() != null) {
                return validateIntegratedPrograms(requestHeaders, redisService.getIntegratedPrograms())
                        .flatMap(aBoolean -> {
                            log.info(INTEGRATED_PROGRAMS_LOADED_FROM_REDIS + aBoolean);
                            if (aBoolean.equals(Boolean.FALSE)) {
                                return redisService.reloadAndGetIntegratedPrograms()
                                        .flatMap(integratedProgramDtos -> integratedProgramDtos.stream()
                                                .filter(integratedProgramDto ->
                                                        integratedProgramDto.getBenefitName().equals(requestHeaders.getBenefitName())
                                                                && integratedProgramDto.getClientId().equals(requestHeaders.getClientId()))
                                                .findAny()
                                                .map(integratedProgramDto -> Mono.just(true))
                                                .orElseThrow(() -> new BenefitNotFoundException(INVALID_BENEFIT_NAME)))
                                        .switchIfEmpty(Mono.error(new BenefitNotFoundException(FAILED_TO_LOAD_INTEGRATED_PROGRAMS)));
                            }
                            return Mono.just(true);
                        });
            }
        }
        return Mono.just(true);
    }

    private Mono<Boolean> validateIntegratedPrograms(RequestHeaders requestHeaders, List<IntegratedProgramDto> integratedProgramDtos) {
        if (integratedProgramDtos != null && !integratedProgramDtos.isEmpty()
                && requestHeaders.getBenefitName() != null && requestHeaders.getClientId() != null) {
            return integratedProgramDtos.stream()
                    .filter(res -> res.getBenefitName().equals(requestHeaders.getBenefitName())
                            && res.getClientId().equals(requestHeaders.getClientId()))
                    .findAny()
                    .map(integratedProgramDto -> Mono.just(true))
                    .orElseThrow(() -> new BenefitNotFoundException(INVALID_BENEFIT_NAME));
        }
        return Mono.just(false);
    }

    private Boolean isValidFacility(RequestHeaders requestHeaders, List<AuthMethods> authMethods, String fToken) {
        return !(((authMethods != null && authMethods.contains(AuthMethods.DEMO))
                && (fToken != null && requestHeaders.getFTokenClaims() != null
                && (requestHeaders.getFTokenClaims().get(ROLES) == null
                || requestHeaders.getFTokenClaims().get(ROLES) != null
                && !requestHeaders.getFTokenClaims().get(ROLES).equals(OFFLINE_HID))))
                || ((authMethods != null && (authMethods.contains(AuthMethods.OTP) || authMethods.contains(AuthMethods.BIO) || authMethods.contains(AuthMethods.IRIS)
                || authMethods.contains(AuthMethods.FACE) || authMethods.contains(AuthMethods.WRONG)))
                && (fToken != null && requestHeaders.getFTokenClaims() != null
                && requestHeaders.getFTokenClaims().get(SUB) == null)));
    }

    private Boolean isValidBenefitRole(RequestHeaders requestHeaders, List<AuthMethods> authMethods) {
        return !(authMethods != null
                && (authMethods.contains(AuthMethods.OTP) || authMethods.contains(AuthMethods.FACE) || authMethods.contains(AuthMethods.BIO) || authMethods.contains(AuthMethods.IRIS) || authMethods.contains(AuthMethods.DEMO))
                && requestHeaders.getBenefitName() != null
                && (requestHeaders.getRoleList() == null || requestHeaders.getRoleList().isEmpty() || !requestHeaders.getRoleList().contains(INTEGRATED_PROGRAM_ROLE)));
    }

    private Mono<EnrolByAadhaarResponseDto> callProcedureToCreateAccount(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto, RequestHeaders requestHeaders) {
        List<AccountDto> accountList = new ArrayList<>();
        List<HidPhrAddressDto> hidPhrAddressDtoList = new ArrayList<>();
        accountList.add(accountDtoResponse);
        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDtoResponse, abhaProfileDto);
        hidPhrAddressDtoList.add(hidPhrAddressDto);
        if (!accountDtoResponse.getHealthIdNumber().isEmpty()) {
            List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_OTP.getValue()));
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.DEMOGRAPHICS.getValue()));
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_BIO.getValue()));
            if (accountDtoResponse.getMobile() != null) {
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
            }

            log.info("going to call procedure to create account");
            return accountService.saveAllData(SaveAllDataRequest.builder().accounts(accountList).hidPhrAddress(hidPhrAddressDtoList).accountAuthMethods(accountAuthMethodsDtos).build()).flatMap(v -> {
                redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
                redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());
                return addAccountAuthMethods(transactionDto, accountDtoResponse, abhaProfileDto, requestHeaders);
            });
        } else {
            throw new AbhaDBGatewayUnavailableException();
        }
    }

    @Override
    public void validateNotificationRequest(SendNotificationRequestDto sendNotificationRequestDto) {
        LinkedHashMap<String, String> errors;
        errors = new LinkedHashMap<>();
        if (!isValidAbhaNumber(sendNotificationRequestDto.getAbhaNumber())) {
            errors.put(ABHA_NUMBER, AbhaConstants.VALIDATION_ERROR_ABHA_NUMBER_FIELD);
        }
        if (!isValidNotificationType(sendNotificationRequestDto.getNotificationType())) {
            errors.put(AbhaConstants.NOTIFICATION_TYPE, VALIDATION_ERROR_NOTIFICATION_TYPE_FIELD);
        }
        if (!isValidType(sendNotificationRequestDto.getType())) {
            errors.put(TYPE, AbhaConstants.VALIDATION_ERROR_TYPE_FIELD);
        }

        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isValidType(String type) {
        return type != null && !type.isEmpty();
    }

    private boolean isValidNotificationType(List<NotificationType> type) {
        if (type != null && !type.isEmpty()) {
            List<NotificationType> notificationTypes = Stream.of(NotificationType.values()).filter(name -> !name.equals(NotificationType.WRONG)).collect(Collectors.toList());
            return new HashSet<>(type).size() == type.size() && Common.isAllNotificationTypeAvailable(notificationTypes, type);

        }
        return false;

    }


    private boolean isValidAbhaNumber(String abhaNumber) {
        if (abhaNumber != null && !abhaNumber.isBlank())
            return Common.isValidAbha(abhaNumber);

        return false;
    }
}
