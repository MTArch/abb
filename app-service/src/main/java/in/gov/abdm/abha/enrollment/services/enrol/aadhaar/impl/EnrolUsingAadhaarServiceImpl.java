package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.KycAuthType;
import in.gov.abdm.abha.enrollment.enums.TransactionStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.application.UnauthorizedUserToSendOrVerifyOtpException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
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
import in.gov.abdm.abha.profile.exception.application.UnAuthorizedException;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.SENT;

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

    @Value("${enrollment.maxMobileLinkingCount:6}")
    private int maxMobileLinkingCount;

    @Override
    public Mono<EnrolByAadhaarResponseDto> verifyOtp(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
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
                            throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS);
                        } else {
                            Mono<AadhaarResponseDto> aadhaarResponseDtoMono =
                                    aadhaarAppService.verifyOtp(AadhaarVerifyOtpRequestDto.builder()
                                            .aadhaarNumber(rsaUtil.encrypt(redisOtp.getReceiver()))
                                            .aadhaarTransactionId(redisOtp.getAadhaarTxnId())
                                            .otp(enrolByAadhaarRequestDto.getAuthData().getOtp().getOtpValue())
                                            .build());

                            return aadhaarResponseDtoMono.flatMap(aadhaarResponseDto -> handleAadhaarOtpResponse(enrolByAadhaarRequestDto, aadhaarResponseDto));
                        }
                    });
        }
    }

    private Mono<EnrolByAadhaarResponseDto> handleAadhaarOtpResponse(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto) {

        handleAadhaarExceptions(aadhaarResponseDto);

        return transactionService.findTransactionDetailsFromDB(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId()).flatMap(transactionDto -> {
            transactionService.mapTransactionWithEkyc(transactionDto, aadhaarResponseDto.getAadhaarUserKycDto(), KycAuthType.OTP.getValue());
            return accountService.findByXmlUid(aadhaarResponseDto.getAadhaarUserKycDto().getSignature())
                    .flatMap(existingAccount -> {
                        if (existingAccount.getStatus().equals(AccountStatus.DELETED.getValue())) {
                            return createNewAccount(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto);
                        } else if (existingAccount.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                            throw new AbhaUnProcessableException(ABDMError.DEACTIVATED_ABHA_ACCOUNT);
                        } else {
                            return existingAccount(transactionDto, aadhaarResponseDto, existingAccount,false);
                        }
                    })
                    .switchIfEmpty(Mono.defer(() -> createNewAccount(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto)));
        });
    }

    private Mono<EnrolByAadhaarResponseDto> existingAccount(TransactionDto transactionDto, AadhaarResponseDto aadhaarResponseDto, AccountDto accountDto,boolean isDeDuplicate) {

        return transactionService.findTransactionDetailsFromDB(String.valueOf(transactionDto.getTxnId()))
                .flatMap(transactionDtoResponse ->
                {
                    transactionDtoResponse.setHealthIdNumber(accountDto.getHealthIdNumber());
                    return transactionService.updateTransactionEntity(transactionDtoResponse, String.valueOf(transactionDto.getTxnId()))
                            .flatMap(res -> {
                                ABHAProfileDto abhaProfileDto = MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), accountDto);
                                Flux<String> fluxPhrAddress = hidPhrAddressService
                                        .getHidPhrAddressByHealthIdNumbersAndPreferredIn(Arrays.asList(accountDto.getHealthIdNumber()), Arrays.asList(1, 0)).map(h -> h.getPhrAddress());

                                return fluxPhrAddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> {
                                    abhaProfileDto.setPhrAddress(phrAddressList);
                                    redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
                                    redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());

                                    //Final response for existing user
                                    if(isDeDuplicate)
                                    {
                                        return Mono.just(EnrolByAadhaarResponseDto.builder()
                                                .txnId(transactionDto.getTxnId().toString())
                                                .abhaProfileDto(abhaProfileDto)
                                                .message(AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST)
                                                .isNew(false)
                                                .build());
                                    }else {
                                        ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                                                .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDto))
                                                .expiresIn(jwtUtil.jwtTokenExpiryTime())
                                                .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                                                .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                                                .build();
                                        return Mono.just(EnrolByAadhaarResponseDto.builder()
                                                .txnId(transactionDto.getTxnId().toString())
                                                .responseTokensDto(responseTokensDto)
                                                .abhaProfileDto(abhaProfileDto)
                                                .message(AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST)
                                                .isNew(false)
                                                .build());
                                    }
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {
        Mono<AccountDto> newAccountDto = lgdUtility.getLgdData(transactionDto.getPincode(), transactionDto.getStateName())
                .flatMap(lgdDistrictResponse -> accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto, lgdDistrictResponse));
        return newAccountDto.flatMap(accountDto -> {

            return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
            .flatMap(duplicateAccount -> {
                if (duplicateAccount.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                    throw new AbhaUnProcessableException(ABDMError.DEACTIVATED_ABHA_ACCOUNT);
                } else {
                    return existingAccount(transactionDto, aadhaarResponseDto, duplicateAccount,true);
                }
            }).switchIfEmpty(Mono.defer(()->{
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
                        // TODO if standard abha
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
                                                .flatMap(transactionDtoResponse -> accountService.createAccountEntity(accountDto))
                                                .flatMap(response -> handleCreateAccountResponse(response, transactionDto, abhaProfileDto));
                                    });
                        } else {
                            //update transaction table and create account in account table
                            //account status is active
                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                    .flatMap(transactionDtoResponse -> accountService.createAccountEntity(accountDto))
                                    .flatMap(response -> handleCreateAccountResponse(response, transactionDto, abhaProfileDto));
                        }

            }));
        });
    }

    private Mono<EnrolByAadhaarResponseDto> updateTransactionEntity(TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {
        Mono<TransactionDto> transactionDtoMono = transactionService.updateTransactionEntity(transactionDto, transactionDto.getTxnId().toString());
        return transactionDtoMono.flatMap(response -> handleUpdateTransactionResponse(response, abhaProfileDto));
    }

    private Mono<EnrolByAadhaarResponseDto> handleUpdateTransactionResponse(TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {
        if (!StringUtils.isEmpty(transactionDto.getAadharNo())) {
            return Mono.just(EnrolByAadhaarResponseDto.builder()
                    .txnId(transactionDto.getTxnId().toString())
                    .abhaProfileDto(abhaProfileDto)
                    .responseTokensDto(new ResponseTokensDto())
                    .build());
        } else {
            throw new AbhaDBGatewayUnavailableException();
        }
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
                                throw new AbhaDBGatewayUnavailableException();
                            }
                        });
            } else {
                throw new AbhaDBGatewayUnavailableException();
            }
        });
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
    public Mono<EnrolByAadhaarResponseDto> faceAuth(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        Mono<AadhaarResponseDto> aadhaarResponseDtoMono = aadhaarAppService.faceAuth(AadhaarVerifyOtpRequestDto.builder()
                .aadhaarNumber(rsaUtil.decrypt(enrolByAadhaarRequestDto.getAuthData().getFace().getAadhaar()))
                .faceAuthPid(enrolByAadhaarRequestDto.getAuthData().getFace().getRdPidData())
                .build());
        return aadhaarResponseDtoMono.flatMap(aadhaarResponseDto -> handleAadhaarFaceResponse(enrolByAadhaarRequestDto, aadhaarResponseDto));
    }

    private Mono<EnrolByAadhaarResponseDto> handleAadhaarFaceResponse(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto) {

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
                    return createNewAccountUsingFAceAuth(enrolByAadhaarRequestDto, aadhaarResponseDto, transaction);
                } else if (existingAccount.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                    throw new AbhaUnProcessableException(ABDMError.DEACTIVATED_ABHA_ACCOUNT);
                } else {
                    return existingAccountFaceAuth(transaction, aadhaarResponseDto, existingAccount,false);
                }
            }).switchIfEmpty(Mono.defer(() -> createNewAccountUsingFAceAuth(enrolByAadhaarRequestDto, aadhaarResponseDto, transaction)));
        });
    }


    private Mono<EnrolByAadhaarResponseDto> createNewAccountUsingFAceAuth(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {
        Mono<AccountDto> newAccountDto = lgdUtility.getLgdData(transactionDto.getPincode(), transactionDto.getStateName())
                .flatMap(lgdDistrictResponse -> accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto, lgdDistrictResponse));
        return newAccountDto.flatMap(accountDto -> {

            return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
                    .flatMap(duplicateAccount -> {
                        if (duplicateAccount.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                            throw new AbhaUnProcessableException(ABDMError.DEACTIVATED_ABHA_ACCOUNT);
                        } else {
                            return existingAccountFaceAuth(transactionDto, aadhaarResponseDto, duplicateAccount,true);
                        }
                    }).switchIfEmpty(Mono.defer(()->{
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
                        // TODO if standard abha
                        {
                            //update transaction table and create account in account table
                            //account status is active
                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                    .flatMap(transactionDtoResponse -> accountService.createAccountEntity(accountDto))
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

    private Mono<EnrolByAadhaarResponseDto> existingAccountFaceAuth(TransactionDto transactionDto, AadhaarResponseDto aadhaarResponseDto, AccountDto accountDto,boolean isDuplicate) {

        return transactionService.findTransactionDetailsFromDB(String.valueOf(transactionDto.getTxnId()))
                .flatMap(transactionDtoResponse ->
                {
                    transactionDtoResponse.setHealthIdNumber(accountDto.getHealthIdNumber());
                    return transactionService.updateTransactionEntity(transactionDtoResponse, String.valueOf(transactionDto.getTxnId()))
                            .flatMap(res -> {
                                ABHAProfileDto abhaProfileDto = MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), accountDto);
                                Flux<String> fluxPhrAddress = hidPhrAddressService
                                        .getHidPhrAddressByHealthIdNumbersAndPreferredIn(Arrays.asList(accountDto.getHealthIdNumber()), Arrays.asList(1, 0)).map(h -> h.getPhrAddress());

                                return fluxPhrAddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> {
                                    abhaProfileDto.setPhrAddress(phrAddressList);
                                    ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                                            .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDto))
                                            .expiresIn(jwtUtil.jwtTokenExpiryTime())
                                            .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                                            .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                                            .build();
                                    //Final response for existing user
                                    if(isDuplicate)
                                        return Mono.just(EnrolByAadhaarResponseDto.builder()
                                                .txnId(transactionDto.getTxnId().toString())
                                                .abhaProfileDto(abhaProfileDto)
                                                .message(AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST)
                                                .isNew(false)
                                                .build());
                                    else
                                        return Mono.just(EnrolByAadhaarResponseDto.builder()
                                                .txnId(transactionDto.getTxnId().toString())
                                                .responseTokensDto(responseTokensDto)
                                                .abhaProfileDto(abhaProfileDto)
                                                .message(AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST)
                                                .isNew(false)
                                                .build());
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }
}
