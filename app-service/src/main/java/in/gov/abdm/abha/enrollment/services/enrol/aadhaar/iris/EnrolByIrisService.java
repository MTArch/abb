package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.iris;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.SUB;
import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.ABHA_RE_ATTEMPTED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import in.gov.abdm.abha.enrollment.enums.request.AadhaarLogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.KycAuthType;
import in.gov.abdm.abha.enrollment.enums.TransactionStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AadhaarMethod;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyBioRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class EnrolByIrisService extends EnrolByIrisValidatorService {

    @Autowired
    AccountService accountService;
    @Autowired
    HidPhrAddressService hidPhrAddressService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    private AccountAuthMethodService accountAuthMethodService;
    @Autowired
    RedisService redisService;
    @Autowired
    AbhaAddressGenerator abhaAddressGenerator;

    private RedisOtp redisOtp;

    @Autowired
    AadhaarAppService aadhaarAppService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    LgdUtility lgdUtility;

    @Value(PropertyConstants.ENROLLMENT_MAX_MOBILE_LINKING_COUNT)
    private int maxMobileLinkingCount;
    @Value(PropertyConstants.ENROLLMENT_IS_TRANSACTION)
    private boolean isTransactionManagementEnable;

    public Mono<EnrolByAadhaarResponseDto> verifyIris(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        if (enrolByAadhaarRequestDto.getAuthData().getIris().getMobile() == null || enrolByAadhaarRequestDto.getAuthData().getIris().getMobile().isBlank()) {
            return verifyAadhaarIris(enrolByAadhaarRequestDto, requestHeaders);
        } else {
            return accountService.getMobileLinkedAccountCount(enrolByAadhaarRequestDto.getAuthData().getIris().getMobile())
                    .flatMap(mobileLinkedAccountCount -> {
                        if (mobileLinkedAccountCount >= maxMobileLinkingCount) {
                            throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS);
                        } else {
                            return verifyAadhaarIris(enrolByAadhaarRequestDto, requestHeaders);
                        }
                    });
        }
    }

    private Mono<EnrolByAadhaarResponseDto> verifyAadhaarIris(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        Mono<AadhaarResponseDto> aadhaarResponseDtoMono = aadhaarAppService.verifyIris(AadhaarVerifyBioRequestDto.builder()
                .aadhaarNumber(rsaUtil.decrypt(enrolByAadhaarRequestDto.getAuthData().getIris().getAadhaar()))
                .pid(enrolByAadhaarRequestDto.getAuthData().getIris().getPid())
                .aadhaarLogType(AadhaarLogType.KYC_I.name())
                .build());
        return aadhaarResponseDtoMono.flatMap(aadhaarResponseDto -> handleAadhaarIrisResponse(enrolByAadhaarRequestDto, aadhaarResponseDto, requestHeaders));
    }

    private Mono<EnrolByAadhaarResponseDto> handleAadhaarIrisResponse(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, RequestHeaders requestHeaders) {

        handleAadhaarExceptions(aadhaarResponseDto);
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setStatus(TransactionStatus.ACTIVE.toString());
        transactionDto.setAadharNo(enrolByAadhaarRequestDto.getAuthData().getIris().getAadhaar());
        transactionDto.setClientIp(Common.getIpAddress());
        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setKycPhoto(Base64.getEncoder().encodeToString(new byte[1]));
        transactionDto.setMobile(aadhaarResponseDto.getAadhaarUserKycDto().getPhone());
        transactionDto.setAadharTxn(aadhaarResponseDto.getAadhaarUserKycDto().getUidiaTxn());
        transactionDto.setCreatedDate(LocalDateTime.now());

        return transactionService.createTransactionEntity(transactionDto).flatMap(transaction -> {
            transactionService.mapTransactionWithEkyc(transaction, aadhaarResponseDto.getAadhaarUserKycDto(), KycAuthType.OTP.getValue());
            return accountService.findByXmlUid(aadhaarResponseDto.getAadhaarUserKycDto().getSignature()).flatMap(existingAccount -> existingAccountIris(transaction, aadhaarResponseDto, existingAccount,requestHeaders)).switchIfEmpty(Mono.defer(() -> createNewAccountUsingIris(enrolByAadhaarRequestDto, aadhaarResponseDto, transaction, requestHeaders)));
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
    @SuppressWarnings("java:S3776")
    private Mono<EnrolByAadhaarResponseDto> createNewAccountUsingIris(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto, RequestHeaders requestHeaders) {
        Mono<AccountDto> newAccountDto = lgdUtility.getLgdData(transactionDto.getPincode(), transactionDto.getStateName())
                .flatMap(lgdDistrictResponse -> accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto, lgdDistrictResponse));
        return newAccountDto.flatMap(accountDto -> {
            accountDto.setFacilityId(requestHeaders.getFTokenClaims() != null && requestHeaders.getFTokenClaims().get(SUB) != null ? requestHeaders.getFTokenClaims().get(SUB).toString() : null);
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
            String userEnteredPhoneNumber = enrolByAadhaarRequestDto.getAuthData().getIris().getMobile();
            if (userEnteredPhoneNumber != null && !userEnteredPhoneNumber.isBlank() && Common.isPhoneNumberMatching(userEnteredPhoneNumber, transactionDto.getMobile())) {
                return aadhaarAppService.verifyDemographicDetails(prepareVerifyDemographicRequest(accountDto, transactionDto, enrolByAadhaarRequestDto))
                        .flatMap(verifyDemographicResponse -> {
                            if (verifyDemographicResponse.isVerified()) {
                                accountDto.setMobile(userEnteredPhoneNumber);
                                abhaProfileDto.setMobile(userEnteredPhoneNumber);
                            }
                            // update transaction table and create account in account table
                            // account status is active
                            if (isTransactionManagementEnable) {
                                return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                        .flatMap(transactionDtoResponse -> accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                        .flatMap(response -> callProcedureToCreateAccount(response, transactionDto, abhaProfileDto));

                            } else {
                                return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                                        .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                                        .flatMap(response -> handleCreateAccountResponseUsingIris(response, transactionDto, abhaProfileDto));
                            }

                        });
            } else {
                // update transaction table and create account in account table
                // account status is active
                if (isTransactionManagementEnable) {
                    return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                            .flatMap(transactionDtoResponse -> accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                            .flatMap(response -> callProcedureToCreateAccount(response, transactionDto, abhaProfileDto));

                } else {
                    return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getTxnId()))
                            .flatMap(transactionDtoResponse -> accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders))
                            .flatMap(response -> handleCreateAccountResponseUsingIris(response, transactionDto, abhaProfileDto));

                }
            }
        });
    }

    private Mono<EnrolByAadhaarResponseDto> handleCreateAccountResponseUsingIris(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {

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
                                return Mono.just(EnrolByAadhaarResponseDto.builder().txnId(transactionDto.getTxnId().toString())
                                        .abhaProfileDto(abhaProfileDto).responseTokensDto(responseTokensDto).isNew(true).build());
                            } else {
                                throw new AbhaDBGatewayUnavailableException();
                            }
                        });
            } else {
                throw new AbhaDBGatewayUnavailableException();
            }
        });
    }

    private Mono<EnrolByAadhaarResponseDto> existingAccountIris(TransactionDto transactionDto, AadhaarResponseDto aadhaarResponseDto, AccountDto accountDto, RequestHeaders rHeaders) {
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
                                    accountService.reAttemptedAbha(abhaProfileDto.getAbhaNumber(), AadhaarMethod.AADHAAR_IIR.code()	, rHeaders)
                        			.onErrorResume(thr -> {
                        		log.info(ABHA_RE_ATTEMPTED, abhaProfileDto.getAbhaNumber());		
                        				return Mono.empty();
                        			}).subscribe();  
                                    abhaProfileDto.setPhrAddress(phrAddressList);
                                    if (!accountDto.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
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
                                                .build());
                                    }
                                    
            
                                    // Final response for existing user
                                    return Mono.just(EnrolByAadhaarResponseDto.builder()
                                            .txnId(transactionDto.getTxnId().toString())
                                            .abhaProfileDto(abhaProfileDto)
                                            .message(AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED)
                                            .build());
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private VerifyDemographicRequest prepareVerifyDemographicRequest(AccountDto accountDto, TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        return VerifyDemographicRequest.builder()
                .aadhaarNumber(rsaUtil.decrypt(transactionDto.getAadharNo()))
                .name(accountDto.getName())
                .phone(enrolByAadhaarRequestDto.getAuthData().getIris().getMobile())
                .build();
    }

    private Mono<EnrolByAadhaarResponseDto> callProcedureToCreateAccount(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {
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
                            ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                                    .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDtoResponse))
                                    .expiresIn(jwtUtil.jwtTokenExpiryTime())
                                    .refreshToken(jwtUtil.generateRefreshToken(accountDtoResponse.getHealthIdNumber()))
                                    .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                                    .build();
                            return Mono.just(EnrolByAadhaarResponseDto.builder().txnId(transactionDto.getTxnId().toString())
                                        .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                                    .abhaProfileDto(abhaProfileDto).responseTokensDto(responseTokensDto).isNew(true).build());
            });
        } else {
            throw new AbhaDBGatewayUnavailableException();
        }
    }


}
