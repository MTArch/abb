package in.gov.abdm.abha.enrollment.services.auth.aadhaar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.error.ABDMError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AuthByAadhaarService {

    private static final String AADHAAR_OTP_INCORRECT_ERROR_CODE = "400";
    private static final String AADHAAR_OTP_EXPIRED_ERROR_CODE = "403";
    private static final String CAN_NOT_LINK_WITH_SAME_ABHA_NUMBER = "Cannot link with same ABHA Number";
    private static final String CAN_NOT_LINK_WITH_CHILD_ABHA_NUMBER = "Cannot link with CHILD ABHA Number";
    public static final String OTP_VERIFIED_SUCCESSFULLY = "OTP verified successfully";

    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    AadhaarClient aadhaarClient;
    @Autowired
    RSAUtil rsaUtil;
    @Autowired
    HidPhrAddressService hidPhrAddressService;

    public Mono<AuthResponseDto> verifyOtpChildAbha(AuthRequestDto authByAadhaarRequestDto) {
        Mono<TransactionDto> txnResponseDto = transactionService.findTransactionDetailsFromDB(authByAadhaarRequestDto.getAuthData().getOtp().getTxnId());
        return txnResponseDto.flatMap(res -> verifyAadhaarOtpChildAbha(res, authByAadhaarRequestDto))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AuthResponseDto> verifyAadhaarOtpChildAbha(TransactionDto transactionDto, AuthRequestDto authByAadhaarRequestDto) {
        Mono<AadhaarResponseDto> aadhaarResponseDtoMono = aadhaarClient.verifyOtp(
                AadhaarVerifyOtpRequestDto.builder().aadhaarNumber(transactionDto.getAadharNo())
                        .aadhaarTransactionId(transactionDto.getAadharTxn())
                        .otp(authByAadhaarRequestDto.getAuthData().getOtp().getOtpValue())
                        .build());

        return aadhaarResponseDtoMono
                .flatMap(res -> HandleAChildAbhaAadhaarOtpResponse(authByAadhaarRequestDto, res, transactionDto));
    }

    private Mono<AuthResponseDto> HandleAChildAbhaAadhaarOtpResponse(AuthRequestDto authByAadhaarRequestDto, AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {
        AuthResponseDto authResponseDto = handleAadhaarExceptions(aadhaarResponseDto, transactionDto.getTxnId().toString());
        if (authResponseDto != null) {
            return Mono.just(authResponseDto);
        } else {
            String encodedXmlUid = Common.base64Encode(aadhaarResponseDto.getAadhaarUserKycDto().getSignature());
            return accountService.findByXmlUid(encodedXmlUid)
                    .flatMap(accountDtoMono -> prepareResponse(accountDtoMono, transactionDto))
                    .flatMap(accountResponseDtoMono -> handleAccountListResponse(authByAadhaarRequestDto, Collections.singletonList(accountResponseDtoMono), transactionDto))
                    .switchIfEmpty(Mono.defer(() -> Mono.just(prepareAuthResponse(transactionDto.getTxnId().toString(), StringConstants.SUCCESS, AbhaConstants.NO_ACCOUNT_FOUND_WITH_AADHAAR_NUMBER, Collections.emptyList()))));
        }
    }


    private Mono<AccountResponseDto> prepareResponse(AccountDto accountDto, TransactionDto transactionDto) {
        int age = Common.calculateYearDifference(accountDto.getYearOfBirth(), accountDto.getMonthOfBirth(),
                accountDto.getDayOfBirth());
        if (age < 18 )
        	throw new AbhaUnProcessableException(ABDMError.CAN_NOT_LINK_WITH_CHILD_ABHA_NUMBER.getCode(), ABDMError.CAN_NOT_LINK_WITH_CHILD_ABHA_NUMBER.getMessage());
        if(accountDto.getHealthIdNumber().equals(transactionDto.getHealthIdNumber()))
            throw new AbhaUnProcessableException(ABDMError.CAN_NOT_LINK_WITH_SAME_ABHA_NUMBER.getCode(), ABDMError.CAN_NOT_LINK_WITH_SAME_ABHA_NUMBER.getMessage());
        
        Flux<String> fluxPhrAaddress = hidPhrAddressService
				.getHidPhrAddressByHealthIdNumbersAndPreferredIn(new ArrayList<>(Collections.singleton(accountDto.getHealthIdNumber())),
						new ArrayList<>(Collections.singleton(1))).map(h -> h.getPhrAddress());

		return fluxPhrAaddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> {
			return Mono.just(
					AccountResponseDto.builder().ABHANumber(accountDto.getHealthIdNumber()).name(accountDto.getName())
							.preferredAbhaAddress(phrAddressList.get(0)).yearOfBirth(accountDto.getYearOfBirth())
							.gender(accountDto.getGender()).mobile(accountDto.getMobile()).email(accountDto.getEmail())
							.kycPhoto(accountDto.getKycPhoto()).build());
		}).switchIfEmpty(Mono.defer(() -> {
			return Mono.just(AccountResponseDto.builder().ABHANumber(accountDto.getHealthIdNumber())
					.name(accountDto.getName()).preferredAbhaAddress(null).yearOfBirth(accountDto.getYearOfBirth())
					.gender(accountDto.getGender()).mobile(accountDto.getMobile()).email(accountDto.getEmail())
					.kycPhoto(accountDto.getKycPhoto()).build());
		}));
        
    }

    private Mono<AuthResponseDto> handleAccountListResponse(AuthRequestDto authByAadhaarRequestDto, List<AccountResponseDto> accountDtoList, TransactionDto transactionDto) {

        List<String> healthIdNumbers = accountDtoList.stream()
                .map(AccountResponseDto::getABHANumber)
                .collect(Collectors.toList());

        return transactionService.findTransactionDetailsFromDB(authByAadhaarRequestDto.getAuthData().getOtp().getTxnId())
                .flatMap(transactionResDto -> {
                    transactionDto.setTxnResponse(healthIdNumbers.stream().collect(Collectors.joining(",")));
                    return transactionService.updateTransactionEntity(transactionDto, authByAadhaarRequestDto.getAuthData().getOtp().getTxnId())
                            .flatMap(response -> AccountResponse(authByAadhaarRequestDto, accountDtoList));
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AuthResponseDto> AccountResponse(AuthRequestDto authByAadhaarRequestDto, List<AccountResponseDto> accountDtoList) {
        if (accountDtoList != null && !accountDtoList.isEmpty()) {
            return Mono.just(AuthResponseDto.builder().txnId(authByAadhaarRequestDto.getAuthData().getOtp().getTxnId())
                    .authResult(StringConstants.SUCCESS)
                    .message(OTP_VERIFIED_SUCCESSFULLY)
                    .accounts(accountDtoList)
                    .build());
        }
        return Mono.empty();
    }

    private AuthResponseDto handleAadhaarExceptions(AadhaarResponseDto aadhaarResponseDto, String transactionId) {
        if (!aadhaarResponseDto.isSuccessful()) {
            switch (aadhaarResponseDto.getAadhaarAuthOtpDto().getErrorCode()) {
                case AADHAAR_OTP_INCORRECT_ERROR_CODE:
                    return prepareAuthResponse(transactionId, StringConstants.FAILED, AbhaConstants.INVALID_AADHAAR_OTP, Collections.emptyList());
                case AADHAAR_OTP_EXPIRED_ERROR_CODE:
                    return prepareAuthResponse(transactionId, StringConstants.FAILED, AbhaConstants.AADHAAR_OTP_EXPIRED, Collections.emptyList());
                default:
                    throw new AadhaarExceptions(aadhaarResponseDto.getAadhaarAuthOtpDto().getErrorCode());
            }
        }
        return null;
    }

    private AuthResponseDto prepareAuthResponse(String transactionId, String authResult, String message, List<AccountResponseDto> accounts) {
        return AuthResponseDto.builder()
                .txnId(transactionId)
                .authResult(authResult)
                .message(message)
                .accounts(accounts)
                .build();
    }
}
