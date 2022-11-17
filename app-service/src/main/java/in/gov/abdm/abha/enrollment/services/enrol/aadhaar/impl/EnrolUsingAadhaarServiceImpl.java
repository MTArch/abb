package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl;

import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.EnrollErrorConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.KycAuthType;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.aadhaar.UidaiException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.AccountNotFoundException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class EnrolUsingAadhaarServiceImpl implements EnrolUsingAadhaarService {

    public static final String FAILED_TO_VERIFY_AADHAAR_OTP = "Failed to Verify Aadhaar OTP";

    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    AadhaarClient aadhaarClient;

    @Override
    public Mono<EnrolByAadhaarResponseDto> verifyOtp(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        return transactionService
                .findTransactionDetailsFromDB(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId())
                .flatMap(transactionDto -> verifyAadhaarOtp(transactionDto, enrolByAadhaarRequestDto));
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
//TODO should not encode and check
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedXmluid = encoder.encodeToString(aadhaarResponseDto.getAadhaarUserKycDto().getSignature().getBytes());
        return accountService.findByXmlUid(encodedXmluid)
                .flatMap(existingAccount -> {
                    return existingAccount(transactionDto, aadhaarResponseDto, existingAccount);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    return createNewAccount(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto);
                }));
    }

    private Mono<EnrolByAadhaarResponseDto> existingAccount(TransactionDto transactionDto, AadhaarResponseDto aadhaarResponseDto, AccountDto accountDto) {
        return Mono.just(EnrolByAadhaarResponseDto.builder()
                .txnId(transactionDto.getTxnId().toString())
                .abhaProfileDto(MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), accountDto))
                .build());
    }

    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {
        AccountDto accountDto = accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto);

        int age = Common.calculateYearDifference(accountDto.getYearOfBirth(), accountDto.getMonthOfBirth(), accountDto.getDayOfBirth());
        if (age >= 18) {
            accountDto.setType(AbhaType.STANDARD);
            accountDto.setStatus(AccountStatus.ACTIVE.toString());
        } else {
            accountDto.setType(AbhaType.CHILD);
            accountDto.setStatus(AccountStatus.PARENT_LINKING_PENDING.toString());
        }

        String newAbhaNumber = AbhaNumberGenerator.generateAbhaNumber();
        accountDto.setHealthIdNumber(newAbhaNumber);
        ABHAProfileDto abhaProfileDto = MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), accountDto);
        //TODO update phr address in db
        abhaProfileDto.setPhrAddress(new ArrayList<>(Collections.singleton(AbhaAddressGenerator.generateDefaultAbhaAddress(newAbhaNumber))));
        // TODO if standard abha
        String userEnteredPhoneNumber = enrolByAadhaarRequestDto.getAuthData().getOtp().getMobile();
        if (Common.isPhoneNumberMatching(userEnteredPhoneNumber, transactionDto.getMobile())) {
            return aadhaarClient.verifyDemographicDetails(prepareVerifyDemographicRequest(accountDto, transactionDto, enrolByAadhaarRequestDto))
                    .flatMap(verifyDemographicResponse -> {
                        if (verifyDemographicResponse.isVerified()) {
                            accountDto.setMobile(userEnteredPhoneNumber);
                        }
                        //update transaction table and create account in account table
                        //account status is active
                        return updateTransactionEntity(transactionDto, abhaProfileDto)
                                .flatMap(transactionResponse-> accountService.createAccountEntity(accountDto))
                                .flatMap(response -> handleCreateAccountResponse(response, transactionDto, abhaProfileDto));
                    });
        } else {
            //update transaction table and create account in account table
            //account status is active
            return updateTransactionEntity(transactionDto, abhaProfileDto)
                    .flatMap(transactionResponse-> accountService.createAccountEntity(accountDto))
                    .flatMap(response -> handleCreateAccountResponse(response, transactionDto, abhaProfileDto));
        }
    }

    private Mono<EnrolByAadhaarResponseDto> updateTransactionEntity(TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {
        Mono<TransactionDto> transactionDtoMono = transactionService.updateTransactionEntity(transactionDto,transactionDto.getTxnId().toString());
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
            throw new DatabaseConstraintFailedException(EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED);
        }
    }

    private Mono<EnrolByAadhaarResponseDto> handleCreateAccountResponse(AccountDto accountDtoResponse, TransactionDto transactionDto, ABHAProfileDto abhaProfileDto) {
        if (!accountDtoResponse.getHealthIdNumber().isEmpty()) {
            return Mono.just(EnrolByAadhaarResponseDto.builder()
                    .txnId(transactionDto.getTxnId().toString())
                    .abhaProfileDto(abhaProfileDto)
                    .responseTokensDto(new ResponseTokensDto())
                    .build());
        } else {
            throw new DatabaseConstraintFailedException(EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED);
        }
    }

    private void handleAadhaarExceptions(AadhaarResponseDto aadhaarResponseDto) {
        if (!aadhaarResponseDto.isSuccessful()) {
            throw new UidaiException(aadhaarResponseDto);
        }
    }

    private VerifyDemographicRequest prepareVerifyDemographicRequest(AccountDto accountDto, TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        return VerifyDemographicRequest.builder()
                .aadhaarNumber(transactionDto.getAadharNo())
                .name(accountDto.getName())
                .phone(enrolByAadhaarRequestDto.getAuthData().getOtp().getMobile())
                .gender(accountDto.getGender())
                .build();
    }

    @Override
    public Mono<AuthByAadhaarResponseDto> verifyOtpChildAbha(AuthByAadhaarRequestDto authByAadhaarRequestDto) {
        Mono<TransactionDto> txnResponseDto = transactionService.findTransactionDetailsFromDB(authByAadhaarRequestDto.getAuthData().getOtp().getTxnId());
        return txnResponseDto.flatMap(res -> verifyAadhaarOtpChildAbha(res, authByAadhaarRequestDto));
    }

    private Mono<AuthByAadhaarResponseDto> verifyAadhaarOtpChildAbha(TransactionDto transactionDto, AuthByAadhaarRequestDto authByAadhaarRequestDto) {
        Mono<AadhaarResponseDto> aadhaarResponseDtoMono = aadhaarClient.verifyOtp(
                AadhaarVerifyOtpRequestDto.builder().aadhaarNumber(transactionDto.getAadharNo())
                        .aadhaarTransactionId(transactionDto.getAadharTxn())
                        .otp(authByAadhaarRequestDto.getAuthData().getOtp().getOtpValue())
                        .build());

        return aadhaarResponseDtoMono
                .flatMap(res -> HandleAChildAbhaAadhaarOtpResponse(authByAadhaarRequestDto, res));
    }

    private Mono<AuthByAadhaarResponseDto> HandleAChildAbhaAadhaarOtpResponse(AuthByAadhaarRequestDto authByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto) {
        handleAadhaarExceptions(aadhaarResponseDto);

        Base64.Encoder encoder = Base64.getEncoder();
        String encodedXmluid = encoder.encodeToString(aadhaarResponseDto.getAadhaarUserKycDto().getSignature().getBytes());

        return accountService.findByXmlUid(encodedXmluid)
                .flatMap(accountDtoMono -> prepareResponse(accountDtoMono))
                .flatMap(accountResponseDtoMono -> handleAccountListResponse(authByAadhaarRequestDto, Collections.singletonList(accountResponseDtoMono)))
                .switchIfEmpty(Mono.error(new AccountNotFoundException(AbhaConstants.ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AccountResponseDto> prepareResponse(AccountDto accountDto) {
        return Mono.just(AccountResponseDto.builder()
                .ABHANumber(accountDto.getHealthIdNumber())
                .name(accountDto.getName())
                .preferredAbhaAddress(accountDto.getPreferredAbhaAddress())
                .yearOfBirth(accountDto.getYearOfBirth())
                .gender(accountDto.getGender())
                .mobile(accountDto.getMobile())
                .email(accountDto.getEmail())
                .build());
    }

    private Mono<AuthByAadhaarResponseDto> handleAccountListResponse(AuthByAadhaarRequestDto authByAadhaarRequestDto, List<AccountResponseDto> accountDtoList) {
        if (accountDtoList != null && !accountDtoList.isEmpty()) {
            return Mono.just(AuthByAadhaarResponseDto.builder().txnId(authByAadhaarRequestDto.getAuthData().getOtp().getTxnId())
                    .authResult(StringConstants.SUCCESS)
                    .accounts(accountDtoList)
                    .build());
        } else {
            return Mono.empty();
        }
    }
}
