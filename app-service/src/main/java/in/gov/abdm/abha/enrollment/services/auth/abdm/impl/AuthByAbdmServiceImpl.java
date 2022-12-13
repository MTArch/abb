package in.gov.abdm.abha.enrollment.services.auth.abdm.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import in.gov.abdm.abha.enrollment.services.auth.abdm.AuthByAbdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.password4j.BadParametersException;

import in.gov.abdm.abha.enrollment.client.IdpClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.EnrollErrorConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.Kyc;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2Util;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;

@Service
public class AuthByAbdmServiceImpl implements AuthByAbdmService {

    private static final String OTP_EXPIRED_RESEND_OTP_AND_RETRY = "OTP expired, please try again.";
    private static final int OTP_EXPIRE_TIME = 10;
    private static final String AUTHORIZATION = "1233";
    private static final String HIP_REQUEST_ID = "22222";
    private static final String REQUEST_ID = "1111";
    private static final String MOBILE_NUMBER_LINKED_SUCCESSFULLY = "Mobile Number linked successfully";
    private static final String OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN = "OTP value did not match, please try again.";
    public static final String FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN = "Failed to Validate OTP, please Try again.";

    private static final String EMAIL_LINKED_SUCCESSFULLY = "Email linked successfully";

    @Autowired
    IdpClient idpClient;
    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountService accountService;


    @Override
    public Mono<AuthResponseDto> verifyOtpViaNotification(AuthRequestDto authByAbdmRequest,boolean isMobile) {
        return transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId())
                .flatMap(transactionDto -> verifyOtpViaNotification(authByAbdmRequest.getAuthData().getOtp().getOtpValue(), transactionDto,isMobile))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AuthResponseDto> verifyOtpViaNotification(String otp, TransactionDto transactionDto,boolean isMobile) {
        try {
            if (GeneralUtils.isOtpExpired(transactionDto.getCreatedDate(), OTP_EXPIRE_TIME)) {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_EXPIRED_RESEND_OTP_AND_RETRY);
            } else if (Argon2Util.verify(transactionDto.getOtp(), otp)) {
                return accountService.getAccountByHealthIdNumber(transactionDto.getHealthIdNumber())
                        .flatMap(accountDto -> updatePhoneNumberInAccountEntity(accountDto, transactionDto,isMobile));
            } else {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN);
            }
        } catch (BadParametersException ex) {
            return prepareAuthByAdbmResponse(transactionDto, false, FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN);
        }
    }

    private Mono<AuthResponseDto> updatePhoneNumberInAccountEntity(AccountDto accountDto, TransactionDto transactionDto,boolean isMobile) {
       String message=null;
        if(isMobile){
            transactionDto.setMobileVerified(Boolean.TRUE);
            accountDto.setMobile(transactionDto.getMobile());
            accountDto.setUpdateDate(now());
            message=MOBILE_NUMBER_LINKED_SUCCESSFULLY;
        }else{
            transactionDto.setEmailVerified(Boolean.TRUE);
            accountDto.setEmail(transactionDto.getEmail());
            accountDto.setEmailVerified("Yes");
            accountDto.setEmailVerificationDate(now());
            accountDto.setUpdateDate(now());
            message=EMAIL_LINKED_SUCCESSFULLY;
        }

        String finalMessage = message;
        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
                .flatMap(transactionDto1 -> accountService.updateAccountByHealthIdNumber(accountDto, accountDto.getHealthIdNumber()))
                .flatMap(accountDto1 -> prepareAuthByAdbmResponse(transactionDto, true, finalMessage));
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
        String otp = authByAbdmRequest.getAuthData().getOtp().getOtpValue();
        String xTransactionId = String.valueOf(transactionDto.getTxnId());
        return idpClient.verifyOtp(otp, AUTHORIZATION, xTransactionId, HIP_REQUEST_ID, REQUEST_ID)
                .flatMap(res -> HandleIdpMobileOtpResponse(authByAbdmRequest, res, transactionDto));
    }

    private Mono<AuthResponseDto> HandleIdpMobileOtpResponse(AuthRequestDto authByAbdmRequest, IdpVerifyOtpResponse idpVerifyOtpResponse, TransactionDto transactionDto) {

        handleIdpServiceExceptions(idpVerifyOtpResponse);

        if (idpVerifyOtpResponse.getKyc() != null && !idpVerifyOtpResponse.getKyc().isEmpty()) {
            List<AccountResponseDto> accountResponseDtoList = prepareResponse(idpVerifyOtpResponse.getKyc());
            return handleAccountListResponse(authByAbdmRequest, accountResponseDtoList, transactionDto);
        }
        return Mono.empty();
    }

    private List<AccountResponseDto> prepareResponse(List<Kyc> kycList) {
        return kycList.stream()
                .filter(kyc -> kyc != null)
                .map(kyc -> MapperUtils.mapKycToAccountResponse(kyc))
                .collect(Collectors.toList());
    }

    private Mono<AuthResponseDto> handleAccountListResponse(AuthRequestDto authByAbdmRequest, List<AccountResponseDto> accountDtoList, TransactionDto transactionDto) {

        if (accountDtoList != null && !accountDtoList.isEmpty()) {
            List<String> healthIdNumbers = accountDtoList.stream()
                    .map(AccountResponseDto::getABHANumber)
                    .collect(Collectors.toList());

            return transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId())
                    .flatMap(transactionResDto -> {
                        transactionDto.setTxnResponse(healthIdNumbers.stream().collect(Collectors.joining(",")));
                        return transactionService.updateTransactionEntity(transactionDto, authByAbdmRequest.getAuthData().getOtp().getTxnId())
                                .flatMap(response -> AccountResponse(authByAbdmRequest, accountDtoList))
                                .switchIfEmpty(Mono.error(new DatabaseConstraintFailedException(EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_UPDATE)));
                    }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
        }
        return Mono.empty();
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

    //TODO -handle idp service exceptions
    private void handleIdpServiceExceptions(IdpVerifyOtpResponse idpVerifyOtpResponse) {
        if (idpVerifyOtpResponse.getResponse() == null) {
            throw new GenericExceptionMessage(AbhaConstants.OTP_VERIFICATION_FAILED_FROM_IDP_EXCEPTION_MESSAGE);
        }
    }
}
