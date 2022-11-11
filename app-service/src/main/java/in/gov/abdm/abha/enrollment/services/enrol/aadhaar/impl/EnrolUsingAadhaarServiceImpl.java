package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl;

import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.KycAuthType;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.aadhaar.UidaiException;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account.impl.AccountServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

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

	public Mono<EnrolByAadhaarResponseDto> HandleAadhaarOtpResponse(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto,
			AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {

		handleAadhaarExceptions(aadhaarResponseDto);

		transactionService.mapTransactionWithEkyc(transactionDto, aadhaarResponseDto.getAadhaarUserKycDto(),
				KycAuthType.OTP.getValue());

		Base64.Encoder encoder = Base64.getEncoder();
		String encodedXmluid = encoder
				.encodeToString(aadhaarResponseDto.getAadhaarUserKycDto().getSignature().getBytes());
		return accountService.findByXmlUid(encodedXmluid).flatMap(response -> {
			return Mono.just(EnrolByAadhaarResponseDto.builder()
					.txnId(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId())
					.abhaProfileDto(MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(), response))
					.responseTokensDto(new ResponseTokensDto()).build());
		}).switchIfEmpty(Mono.defer(() -> {
			AccountDto existingAccountDto = accountService.prepareNewAccount(transactionDto, enrolByAadhaarRequestDto);

			int age = Common.calculateYearDifference(Integer.parseInt(existingAccountDto.getYearOfBirth()),
					Integer.parseInt(existingAccountDto.getMonthOfBirth()),
					Integer.parseInt(existingAccountDto.getDayOfBirth()), LocalDate.now());
			if (age >= 18) {
				existingAccountDto.setType(AbhaType.STANDARD);
				existingAccountDto.setStatus(AccountStatus.ACTIVE.toString());
			} else {
				existingAccountDto.setType(AbhaType.CHILD);
				existingAccountDto.setStatus(AccountStatus.PARENT_LINKING_PENDING.toString());
			}

			String newAbhaNumber = AbhaNumberGenerator.generateAbhaNumber();
			existingAccountDto.setHealthIdNumber(newAbhaNumber);
			ABHAProfileDto abhaProfileDto = MapperUtils.mapKycDetails(aadhaarResponseDto.getAadhaarUserKycDto(),
					existingAccountDto);
			// TODO update phr address in db
			abhaProfileDto.setPhrAddress(new ArrayList<>(
					Collections.singleton(AbhaAddressGenerator.generateDefaultAbhaAddress(newAbhaNumber))));
			// TODO call account db update
			Mono<AccountDto> accountDtoResponse = accountService.createAccountEntity(existingAccountDto);
			// TODO call transaction db service
			// TODO delete transaction
			return accountDtoResponse
					.flatMap(res -> handleCreateAccountResponse(res, enrolByAadhaarRequestDto, abhaProfileDto));
		}));
	}

    private Mono<EnrolByAadhaarResponseDto> handleCreateAccountResponse(AccountDto accountDtoResponse, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, ABHAProfileDto abhaProfileDto) {
        if (!accountDtoResponse.getHealthIdNumber().isEmpty()) {
            return Mono.just(EnrolByAadhaarResponseDto.builder()
                    .txnId(enrolByAadhaarRequestDto.getAuthData().getOtp().getTxnId())
                    .abhaProfileDto(abhaProfileDto)
                    .responseTokensDto(new ResponseTokensDto())
                    .build());
        } else {
            return Mono.empty();
        }
    }

    private void handleAadhaarExceptions(AadhaarResponseDto aadhaarResponseDto) {
        if (!aadhaarResponseDto.isSuccessful()) {
            throw new UidaiException(aadhaarResponseDto);
        }
    }
}
