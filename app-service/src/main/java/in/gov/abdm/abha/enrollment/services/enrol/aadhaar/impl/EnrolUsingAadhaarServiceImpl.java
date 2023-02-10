package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.KycAuthType;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.application.UnauthorizedUserToSendOrVerifyOtpException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
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
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.services.lgd.LgdAppService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class EnrolUsingAadhaarServiceImpl implements EnrolUsingAadhaarService {

    private static final String FAILED_TO_VERIFY_AADHAAR_OTP = "Failed to Verify Aadhaar OTP";

    private static final String AADHAAR_OTP_INCORRECT_ERROR_CODE = "400";

    private static final String AADHAAR_OTP_EXPIRED_ERROR_CODE = "403";


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

    private RedisOtp redisOtp;

    @Autowired
    LgdAppService lgdAppService;

    @Autowired
    AadhaarAppService aadhaarAppService;

    @Override
    public Mono<EnrolByAadhaarResponseDto> verifyOtp(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        redisOtp = redisService.getRedisOtp(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId());
        if (redisOtp == null) {
            throw new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE);
        } else {
            if (!redisService.isMultipleOtpVerificationAllowed(redisOtp.getReceiver())) {
                throw new UnauthorizedUserToSendOrVerifyOtpException();
            }
            Mono<AadhaarResponseDto> aadhaarResponseDtoMono =
                    aadhaarAppService.verifyOtp(AadhaarVerifyOtpRequestDto.builder()
                            .aadhaarNumber(rsaUtil.encrypt(redisOtp.getReceiver()))
                            .aadhaarTransactionId(redisOtp.getAadhaarTxnId())
                            .otp(enrolByAadhaarRequestDto.getAuthData().getOtp().getOtpValue())
                            .build());

            return aadhaarResponseDtoMono.flatMap(aadhaarResponseDto -> handleAadhaarOtpResponse(enrolByAadhaarRequestDto, aadhaarResponseDto));
        }
    }

    private Mono<EnrolByAadhaarResponseDto> handleAadhaarOtpResponse(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto) {

        handleAadhaarExceptions(aadhaarResponseDto);

        return transactionService.findTransactionDetailsFromDB(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId()).flatMap(transactionDto -> {
            transactionService.mapTransactionWithEkyc(transactionDto, aadhaarResponseDto.getAadhaarUserKycDto(), KycAuthType.OTP.getValue());
            String encodedXmlUid = Common.base64Encode(aadhaarResponseDto.getAadhaarUserKycDto().getSignature());
            return accountService.findByXmlUid(encodedXmlUid)
                    .flatMap(existingAccount -> {
                        return existingAccount(transactionDto, aadhaarResponseDto, existingAccount);
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        return createNewAccount(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto);
                    }));
        });
    }

    private Mono<EnrolByAadhaarResponseDto> existingAccount(TransactionDto transactionDto, AadhaarResponseDto aadhaarResponseDto, AccountDto accountDto) {

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
                                    return Mono.just(EnrolByAadhaarResponseDto.builder()
                                            .txnId(transactionDto.getTxnId().toString())
                                            .abhaProfileDto(abhaProfileDto)
                                            .build());
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {
        Mono<AccountDto> newAccountDto = lgdAppService.getDetailsByAttribute(transactionDto.getPincode(),"District")
                .flatMap(lgdDistrictResponse -> accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto, lgdDistrictResponse));
        return newAccountDto.flatMap(accountDto -> {
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

        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(transactionDto, accountDtoResponse, abhaProfileDto);

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
                                return Mono.just(EnrolByAadhaarResponseDto.builder().txnId(transactionDto.getTxnId().toString())
                                        .abhaProfileDto(abhaProfileDto).responseTokensDto(new ResponseTokensDto()).build());
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
}
