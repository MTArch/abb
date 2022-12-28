package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl;

import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.client.LGDClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.EnrollErrorConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.KycAuthType;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    AadhaarClient aadhaarClient;
    @Autowired
    RSAUtil rsaUtil;
    @Autowired
    private LGDClient lgdClient;

    @Override
    public Mono<EnrolByAadhaarResponseDto> verifyOtp(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        return transactionService
                .findTransactionDetailsFromDB(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId())
                .flatMap(transactionDto -> verifyAadhaarOtp(transactionDto, enrolByAadhaarRequestDto))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<EnrolByAadhaarResponseDto> verifyAadhaarOtp(TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        Mono<AadhaarResponseDto> aadhaarResponseDtoMono = aadhaarClient.verifyOtp(
                AadhaarVerifyOtpRequestDto.builder().aadhaarNumber(transactionDto.getAadharNo())
                        .aadhaarTransactionId(transactionDto.getAadharTxn())
                        .otp(enrolByAadhaarRequestDto.getAuthData().getOtp().getOtpValue())
                        .build());

        return aadhaarResponseDtoMono
                .flatMap(res -> HandleAadhaarOtpResponse(enrolByAadhaarRequestDto, res, transactionDto));
    }

    private Mono<EnrolByAadhaarResponseDto> HandleAadhaarOtpResponse(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {

        handleAadhaarExceptions(aadhaarResponseDto);

        transactionService.mapTransactionWithEkyc(transactionDto, aadhaarResponseDto.getAadhaarUserKycDto(), KycAuthType.OTP.getValue());
        String encodedXmlUid = Common.base64Encode(aadhaarResponseDto.getAadhaarUserKycDto().getSignature());
        return accountService.findByXmlUid(encodedXmlUid)
                .flatMap(existingAccount -> {
                    return existingAccount(transactionDto, aadhaarResponseDto, existingAccount);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    return createNewAccount(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto);
                }));
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
                                    return Mono.just(EnrolByAadhaarResponseDto.builder()
                                            .txnId(transactionDto.getTxnId().toString())
                                            .abhaProfileDto(abhaProfileDto)
                                            .build());
                                });
                            }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {
        Mono<AccountDto> newAccountDto = lgdClient.getLgdDistrictDetails(transactionDto.getPincode())
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
            String defaultAbhaAddress = AbhaAddressGenerator.generateDefaultAbhaAddress(newAbhaNumber);
            accountDto.setHealthId(defaultAbhaAddress);
            abhaProfileDto.setPhrAddress(new ArrayList<>(Collections.singleton(defaultAbhaAddress)));
            abhaProfileDto.setStateCode(accountDto.getStateCode());
            abhaProfileDto.setDistrictCode(accountDto.getDistrictCode());
            // TODO if standard abha
            String userEnteredPhoneNumber = enrolByAadhaarRequestDto.getAuthData().getOtp().getMobile();
            if (Common.isPhoneNumberMatching(userEnteredPhoneNumber, transactionDto.getMobile())) {
                return aadhaarClient.verifyDemographicDetails(prepareVerifyDemographicRequest(accountDto, transactionDto, enrolByAadhaarRequestDto))
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
            throw new DatabaseConstraintFailedException(EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_CREATE);
        }
    }

    private Mono<EnrolByAadhaarResponseDto> handleCreateAccountResponse(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {

        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(transactionDto, accountDtoResponse, abhaProfileDto);

        return hidPhrAddressService.createHidPhrAddressEntity(hidPhrAddressDto).flatMap(response -> {
            if (!accountDtoResponse.getHealthIdNumber().isEmpty()) {
                return Mono.just(EnrolByAadhaarResponseDto.builder().txnId(transactionDto.getTxnId().toString())
                        .abhaProfileDto(abhaProfileDto).responseTokensDto(new ResponseTokensDto()).build());
            } else {
                throw new DatabaseConstraintFailedException(
                        EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_UPDATE);
            }
        });


    }

    private void handleAadhaarExceptions(AadhaarResponseDto aadhaarResponseDto) {
        if (!aadhaarResponseDto.isSuccessful()) {
            if (aadhaarResponseDto.getAadhaarAuthOtpDto() != null)
                throw new AadhaarExceptions(aadhaarResponseDto.getAadhaarAuthOtpDto().getErrorCode());
            else
                throw new AadhaarExceptions(aadhaarResponseDto.getErrorCode());
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
