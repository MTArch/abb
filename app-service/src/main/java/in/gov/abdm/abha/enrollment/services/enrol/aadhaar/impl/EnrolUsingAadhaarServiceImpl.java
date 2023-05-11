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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;

@Service
@Slf4j
public class EnrolUsingAadhaarServiceImpl implements EnrolUsingAadhaarService {

    private static final String NOTIFICATION_SENT_ON_ACCOUNT_CREATION = "Notification sent successfully on Account Creation";
    private static final String ON_MOBILE_NUMBER = "on Mobile Number:";
    private static final String FOR_HEALTH_ID_NUMBER = "for HealthIdNumber:";

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
    @Qualifier(AbhaConstants.INTEGRATED_PROGRAMS)
    private List<IntegratedProgramDto> integratedProgramDtos;

    @Value(PropertyConstants.ENROLLMENT_MAX_MOBILE_LINKING_COUNT)
    private int maxMobileLinkingCount;

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
                        } else {
                            return existingAccount(transactionDto, aadhaarResponseDto, existingAccount, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST);
                        }
                    })
                    .switchIfEmpty(Mono.defer(() -> createNewAccount(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto, requestHeaders)));
        });
    }

    private Mono<EnrolByAadhaarResponseDto> existingAccount(TransactionDto transactionDto, AadhaarResponseDto aadhaarResponseDto, AccountDto accountDto, boolean generateToken, String responseMessage) {

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

                                    //Final response for existing user
                                    return Mono.just(enrolByAadhaarResponseDto);
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto, RequestHeaders requestHeaders) {
        Mono<AccountDto> newAccountDto = lgdUtility.getLgdData(transactionDto.getPincode(), transactionDto.getStateName())
                .flatMap(lgdDistrictResponse -> accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto, lgdDistrictResponse));
        return newAccountDto.flatMap(accountDto -> {

            return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
                    .flatMap(duplicateAccount -> {
                        return existingAccount(transactionDto, aadhaarResponseDto, duplicateAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST);
                    }).switchIfEmpty(Mono.defer(() -> {
                        int age = Common.calculateYearDifference(accountDto.getYearOfBirth(), accountDto.getMonthOfBirth(), accountDto.getDayOfBirth());
                        if (age >= 18) {
                            accountDto.setType(AbhaType.STANDARD);
                            accountDto.setStatus(AccountStatus.ACTIVE.toString());
                        } else {
                            accountDto.setType(AbhaType.CHILD);
                            accountDto.setStatus(AccountStatus.PARENT_LINKING_PENDING.toString());
                        }

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
                                        //update transaction table and create account in account table
                                        //account status is active
                                        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                                .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                                .flatMap(response -> handleCreateAccountResponse(response, transactionDto, abhaProfileDto));
                                    });
                        } else {
                            //update transaction table and create account in account table
                            //account status is active
                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                    .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                    .flatMap(response -> handleCreateAccountResponse(response, transactionDto, abhaProfileDto));
                        }

                    }));
        });
    }


    private Mono<EnrolByAadhaarResponseDto> handleCreateAccountResponse(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {

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
                                redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
                                redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());

                                return addAccountAuthMethods(transactionDto, accountDtoResponse, abhaProfileDto);

                            } else {
                                throw new AbhaDBGatewayUnavailableException();
                            }
                        });
            } else {
                throw new AbhaDBGatewayUnavailableException();
            }
        });
    }

    private Mono<EnrolByAadhaarResponseDto> addAccountAuthMethods(TransactionDto transactionDto, AccountDto accountDtoResponse, ABHAProfileDto abhaProfileDto) {
        if (accountDtoResponse.getMobile() != null && !accountDtoResponse.getMobile().isBlank()) {
            return notificationService.sendRegistrationSMS(accountDtoResponse.getMobile(), accountDtoResponse.getName(), accountDtoResponse.getHealthIdNumber())
                    .flatMap(notificationResponseDto -> {
                        if (notificationResponseDto.getStatus().equals(SENT)) {
                            log.info(NOTIFICATION_SENT_ON_ACCOUNT_CREATION + ON_MOBILE_NUMBER + accountDtoResponse.getMobile() + FOR_HEALTH_ID_NUMBER + accountDtoResponse.getHealthIdNumber());
                            ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                                    .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDtoResponse))
                                    .expiresIn(jwtUtil.jwtTokenExpiryTime())
                                    .refreshToken(jwtUtil.generateRefreshToken(accountDtoResponse.getHealthIdNumber()))
                                    .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                                    .build();
                            //final create new account response
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
            //final create new account response
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
        return accountService.getMobileLinkedAccountCount(enrolByAadhaarRequestDto.getAuthData().getFace().getMobile())
                .flatMap(mobileLinkedAccountCount -> {
                    if (mobileLinkedAccountCount >= maxMobileLinkingCount) {
                        throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS);
                    } else {
                        Mono<AadhaarResponseDto> aadhaarResponseDtoMono = aadhaarAppService.faceAuth(AadhaarVerifyFaceAuthRequestDto.builder()
                                .aadhaarNumber(rsaUtil.decrypt(enrolByAadhaarRequestDto.getAuthData().getFace().getAadhaar()))
                                .faceAuthPid(enrolByAadhaarRequestDto.getAuthData().getFace().getRdPidData())
                                .build());
                        return aadhaarResponseDtoMono.flatMap(aadhaarResponseDto -> handleAadhaarFaceResponse(enrolByAadhaarRequestDto, aadhaarResponseDto, requestHeaders));
                    }
                });
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

            return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
                    .flatMap(duplicateAccount -> {
                        return existingAccountFaceAuth(transactionDto, aadhaarResponseDto, duplicateAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST);
                    }).switchIfEmpty(Mono.defer(() -> {
                        int age = Common.calculateYearDifference(accountDto.getYearOfBirth(), accountDto.getMonthOfBirth(), accountDto.getDayOfBirth());
                        if (age >= 18) {
                            accountDto.setType(AbhaType.STANDARD);
                            accountDto.setStatus(AccountStatus.ACTIVE.toString());
                        } else {
                            accountDto.setType(AbhaType.CHILD);
                            accountDto.setStatus(AccountStatus.PARENT_LINKING_PENDING.toString());
                        }

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
                        if (Common.isPhoneNumberMatching(userEnteredPhoneNumber, transactionDto.getMobile())) {
                            return aadhaarAppService.verifyDemographicDetails(prepareVerifyDemographicRequest(accountDto, transactionDto, enrolByAadhaarRequestDto))
                                    .flatMap(verifyDemographicResponse -> {
                                        if (verifyDemographicResponse.isVerified()) {
                                            accountDto.setMobile(userEnteredPhoneNumber);
                                            abhaProfileDto.setMobile(userEnteredPhoneNumber);
                                        }
                                        //update transaction table and create account in account table
                                        //account status is active
                                        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                                .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                                .flatMap(response -> handleCreateAccountResponseUsingFaceAuth(response, transactionDto, abhaProfileDto));
                                    });
                        } else {
                            //update transaction table and create account in account table
                            //account status is active
                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                    .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                    .flatMap(response -> handleCreateAccountResponseUsingFaceAuth(response, transactionDto, abhaProfileDto));
                        }
                    }));
        });
    }

    private Mono<EnrolByAadhaarResponseDto> handleCreateAccountResponseUsingFaceAuth(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {

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
                                ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                                        .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDtoResponse))
                                        .expiresIn(jwtUtil.jwtTokenExpiryTime())
                                        .refreshToken(jwtUtil.generateRefreshToken(accountDtoResponse.getHealthIdNumber()))
                                        .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                                        .build();
                                return Mono.just(EnrolByAadhaarResponseDto.builder()
                                        .txnId(transactionDto.getTxnId().toString())
                                        .abhaProfileDto(abhaProfileDto)
                                        .responseTokensDto(responseTokensDto)
                                        .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                                        .isNew(true)
                                        .build());
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
                                    //Final response for existing user
                                    return Mono.just(enrolByAadhaarResponseDto);
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    @Override
    public void validateHeaders(RequestHeaders requestHeaders, List<AuthMethods> authMethods, String fToken) {
        if (authMethods.contains(AuthMethods.OTP)) {
            isValidBenefitProgram(requestHeaders);
            isValidFacility(requestHeaders, fToken);
        } else if (authMethods.contains(AuthMethods.DEMO)) {
            isAuthorized(requestHeaders, fToken);
            isValidBenefitProgram(requestHeaders);
            isValidFacilityForDemoAuth(requestHeaders, fToken);
        } else if (authMethods.contains(AuthMethods.BIO)) {
            isAuthorized(requestHeaders, fToken);
            isValidBenefitProgram(requestHeaders);
            isValidFacility(requestHeaders, fToken);
        } else if (authMethods.contains(AuthMethods.FACE)) {
            if (fToken == null || fToken.equals(StringConstants.EMPTY)) {
                throw new AbhaUnAuthorizedException(ABDMError.UNAUTHORIZED_ACCESS.getCode(), ABDMError.UNAUTHORIZED_ACCESS.getMessage());
            }
            isValidFacility(requestHeaders, fToken);
        } else {
            //DL
            isValidFacility(requestHeaders, fToken);
        }
    }

    private void isAuthorized(RequestHeaders requestHeaders, String fToken) {
        if ((requestHeaders.getBenefitName() == null || requestHeaders.getBenefitName().equals(StringConstants.EMPTY))
                && (fToken == null || fToken.equals(StringConstants.EMPTY))) {
            throw new AbhaUnAuthorizedException(ABDMError.UNAUTHORIZED_ACCESS.getCode(), ABDMError.UNAUTHORIZED_ACCESS.getMessage());
        }
    }

    private void isValidFacilityForDemoAuth(RequestHeaders requestHeaders, String fToken) {
        if (fToken != null && requestHeaders.getFTokenClaims() != null
                && (requestHeaders.getFTokenClaims().get(ROLES) == null
                || requestHeaders.getFTokenClaims().get(ROLES) != null && !requestHeaders.getFTokenClaims().get(ROLES).equals(OFFLINE_HID))) {
            throw new AbhaUnAuthorizedException(ABDMError.INVALID_F_TOKEN.getCode(), ABDMError.INVALID_F_TOKEN.getMessage());
        }
    }

    private void isValidFacility(RequestHeaders requestHeaders, String fToken) {
        if (fToken != null && requestHeaders.getFTokenClaims() != null
                && requestHeaders.getFTokenClaims().get(SUB) == null) {
            throw new AbhaUnAuthorizedException(ABDMError.INVALID_F_TOKEN.getCode(), ABDMError.INVALID_F_TOKEN.getMessage());
        }
    }

    private void isValidBenefitProgram(RequestHeaders requestHeaders) {
        if (requestHeaders.getBenefitName() != null && integratedProgramDtos != null
                && integratedProgramDtos.stream().noneMatch(res -> res.getBenefitName().equals(requestHeaders.getBenefitName())
                && res.getClientId().equals(requestHeaders.getClientId()))
                || requestHeaders.getRoleList() != null && !requestHeaders.getRoleList().contains(INTEGRATED_PROGRAM_ROLE)) {
            throw new BenefitNotFoundException(ABDMError.BENEFIT_NOT_FOUND.getCode(), ABDMError.BENEFIT_NOT_FOUND.getMessage());
        }
    }
}
