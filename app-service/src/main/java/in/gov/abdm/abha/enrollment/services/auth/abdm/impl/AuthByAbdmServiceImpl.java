package in.gov.abdm.abha.enrollment.services.auth.abdm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.password4j.BadParametersException;

import in.gov.abdm.abha.enrollment.client.IdpClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.EnrollErrorConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.Kyc;
import in.gov.abdm.abha.enrollment.services.auth.abdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AuthByAbdmServiceImpl implements AuthByAbdmService {

    private static final String OTP_EXPIRED_RESEND_OTP_AND_RETRY = "OTP expired, please try again.";
    private static final int OTP_EXPIRE_TIME = 10;
    private static final String AUTHORIZATION = "1233";
    private static final String HIP_REQUEST_ID = "22222";

    private static final String MOBILE_NUMBER_LINKED_SUCCESSFULLY = "Mobile Number linked successfully";
    private static final String OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN = "OTP value did not match, please try again.";
    public static final String FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN = "Failed to Validate OTP, please Try again.";

    @Autowired
    IdpClient idpClient;
    
    @Autowired
    TransactionService transactionService;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    HidPhrAddressService hidPhrAddressService;


    @Override
    public Mono<AuthResponseDto> verifyOtpViaNotification(AuthRequestDto authByAbdmRequest) {
        return transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId())
                .flatMap(transactionDto -> verifyOtpViaNotification(authByAbdmRequest.getAuthData().getOtp().getOtpValue(), transactionDto))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AuthResponseDto> verifyOtpViaNotification(String otp, TransactionDto transactionDto) {
        try {
            if (GeneralUtils.isOtpExpired(transactionDto.getCreatedDate(), OTP_EXPIRE_TIME)) {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_EXPIRED_RESEND_OTP_AND_RETRY);
            } else if (Argon2Util.verify(transactionDto.getOtp(), otp)) {
                return accountService.getAccountByHealthIdNumber(transactionDto.getHealthIdNumber())
                        .flatMap(accountDto -> updatePhoneNumberInAccountEntity(accountDto, transactionDto));
            } else {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN);
            }
        } catch (BadParametersException ex) {
            return prepareAuthByAdbmResponse(transactionDto, false, FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN);
        }
    }

    private Mono<AuthResponseDto> updatePhoneNumberInAccountEntity(AccountDto accountDto, TransactionDto transactionDto) {
        transactionDto.setMobileVerified(true);
        accountDto.setMobile(transactionDto.getMobile());
        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
                .flatMap(transactionDto1 -> accountService.updateAccountByHealthIdNumber(accountDto, accountDto.getHealthIdNumber()))
                .flatMap(accountDto1 -> prepareAuthByAdbmResponse(transactionDto, true, MOBILE_NUMBER_LINKED_SUCCESSFULLY));
    }

    private Mono<AuthResponseDto> prepareAuthByAdbmResponse(TransactionDto transactionDto, boolean status, String message) {

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
                .accounts(status ? Collections.singletonList(accountResponseDto) : Collections.emptyList())
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
		return idpClient.verifyOtp(idpVerifyOtpRequest, AUTHORIZATION, xTransactionId, HIP_REQUEST_ID, requestId)
				.flatMap(res -> HandleIdpMobileOtpResponse(authByAbdmRequest, res, transactionDto));
	}

	private Mono<AuthResponseDto> HandleIdpMobileOtpResponse(AuthRequestDto authByAbdmRequest,
			IdpVerifyOtpResponse idpVerifyOtpResponse, TransactionDto transactionDto) {

		if (idpVerifyOtpResponse.getResponse() == null) {
			return Mono.just(prepareAuthResponse(transactionDto.getTxnId().toString(), StringConstants.FAILED,
					AbhaConstants.INVALID_OTP, Collections.emptyList()));
		} else {
			List<String> healthIdNumbers = idpVerifyOtpResponse.getKyc().stream()
					.filter(kyc -> !kyc.getAbhaNumber().equals(transactionDto.getHealthIdNumber()))
					.map(Kyc::getAbhaNumber).collect(Collectors.toList());
			
			if (healthIdNumbers.size() == 0)
				return Mono.just(prepareAuthResponse(transactionDto.getTxnId().toString(), StringConstants.SUCCESS,
					AbhaConstants.NO_ACCOUNT_FOUND, Collections.emptyList()));

			Flux<AccountDto> accountDtoFlux = accountService.getAccountsByHealthIdNumbers(healthIdNumbers);

			return accountDtoFlux.collectList().flatMap(Mono::just).flatMap(accountDtoList -> {

				Flux<HidPhrAddressDto> fluxPhrAaddress = hidPhrAddressService
						.getHidPhrAddressByHealthIdNumbersAndPreferredIn(healthIdNumbers,
								new ArrayList<>(Collections.singleton(1)));

				return fluxPhrAaddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> {

					return handleAccountListResponse(authByAbdmRequest, accountDtoList, phrAddressList, healthIdNumbers,
							transactionDto);
				}).switchIfEmpty(Mono.defer(() -> {
					
					return handleAccountListResponse(authByAbdmRequest, accountDtoList, Collections.emptyList(),
							healthIdNumbers, transactionDto);
				}));

			}).switchIfEmpty(Mono.defer(() -> {
				return Mono.just(prepareAuthResponse(transactionDto.getTxnId().toString(), StringConstants.SUCCESS,
						AbhaConstants.NO_ACCOUNT_FOUND, Collections.emptyList()));
			}));
		}
	}

    private Mono<AuthResponseDto> handleAccountListResponse(AuthRequestDto authByAbdmRequest, List<AccountDto> accountDtoList, List<HidPhrAddressDto> phrAddressList, List<String> healthIdNumbers, TransactionDto transactionDto) {
		if (accountDtoList != null && !accountDtoList.isEmpty()) {
			transactionDto.setTxnResponse(healthIdNumbers.stream().collect(Collectors.joining(",")));

			List<AccountResponseDto> accountResponseDtoList = prepareAccountResponseDtoList(accountDtoList,
					phrAddressList);

			return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
					.flatMap(response -> AccountResponse(authByAbdmRequest, accountResponseDtoList))
					.switchIfEmpty(Mono.error(new DatabaseConstraintFailedException(
							EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_UPDATE)));
		}
        return Mono.empty();
    }

    private List<AccountResponseDto> prepareAccountResponseDtoList(List<AccountDto> accountDtoList,
			List<HidPhrAddressDto> phrAddressList) {

		List<AccountResponseDto> accountResponseDtos = accountDtoList.stream().map(accountDto -> {
			Optional<String> reducedValue = phrAddressList.stream()
					.filter(hidPhrAddDto -> hidPhrAddDto.getHealthIdNumber().equals(accountDto.getHealthIdNumber()))
					.map(hidPhrAddDto -> hidPhrAddDto.getPhrAddress()).reduce((first, next) -> first);

			return MapperUtils.mapAccountDtoToAccountResponse(accountDto,
					reducedValue.isPresent() ? reducedValue.get() : StringConstants.EMPTY);
		}).collect(Collectors.toList());

		return accountResponseDtos;
	}

	private Mono<AuthResponseDto> AccountResponse(AuthRequestDto authByAbdmRequest, List<AccountResponseDto> accountDtoList) {
        if (accountDtoList != null && !accountDtoList.isEmpty() && accountDtoList.size() > 0) {
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
}
