package in.gov.abdm.abha.enrollment.services.auth_byabdm.impl;

import com.password4j.BadParametersException;
import in.gov.abdm.abha.enrollment.client.IdpClient;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.auth_byabdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class AuthByAbdmServiceImpl implements AuthByAbdmService {

    private static final String OTP_EXPIRED_RESEND_OTP_AND_RETRY = "OTP expired, resend OTP and retry";
    private static final int OTP_EXPIRE_TIME = 10;
    
    @Autowired
    IdpClient idpClient;
    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountService accountService;

    @Override
    public Mono<AuthResponseDto> verifyOtpViaNotification(AuthByAbdmRequest authByAbdmRequest) {
        return transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId())
                .flatMap(transactionDto -> verifyOtpViaNotification(authByAbdmRequest.getAuthData().getOtp().getOtpValue(), transactionDto));
    }

    private Mono<AuthResponseDto> verifyOtpViaNotification(String otp, TransactionDto transactionDto) {
        try {
            if (GeneralUtils.isOtpExpired(transactionDto.getCreatedDate(), OTP_EXPIRE_TIME)) {
                throw new GenericExceptionMessage(OTP_EXPIRED_RESEND_OTP_AND_RETRY);
            } else if (Argon2Util.verify(transactionDto.getOtp(), otp)) {
                //todo instead getOidcRedirectUrl need to access health id
                return accountService.getAccountByHealthIdNumber(transactionDto.getHealthIdNumber())
                        .flatMap(accountDto -> updatePhoneNumberInAccountEntity(accountDto, transactionDto));
            } else {
                return prepareAuthByAdbmResponse(transactionDto, false);
            }
        } catch (BadParametersException ex) {
            return prepareAuthByAdbmResponse(transactionDto, false);
        }
    }

    private Mono<AuthResponseDto> updatePhoneNumberInAccountEntity(AccountDto accountDto, TransactionDto transactionDto) {
        transactionDto.setMobileVerified(true);
        accountDto.setMobile(transactionDto.getMobile());
        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
                .flatMap(transactionDto1 -> accountService.updateAccountByHealthIdNumber(accountDto, accountDto.getHealthIdNumber()))
                .flatMap(accountDto1 -> prepareAuthByAdbmResponse(transactionDto, true));
    }

    private Mono<AuthResponseDto> prepareAuthByAdbmResponse(TransactionDto transactionDto, boolean status) {

        AccountResponseDto accountResponseDto = null;

        if (status) {
            accountResponseDto = AccountResponseDto.builder()
                    //TODO get Health id number instead of getOidcRedirectUrl
                    .ABHANumber(transactionDto.getHealthIdNumber())
                    .name(transactionDto.getName())
                    .build();
        }

        return Mono.just(AuthResponseDto.builder()
                .txnId(transactionDto.getTxnId().toString())
                .authResult(status ? StringConstants.SUCCESS : StringConstants.FAILED)
                .accounts(status ? Collections.singletonList(accountResponseDto) : Collections.emptyList())
                .build());
    }

    @Override
    public Mono<AuthResponseDto> verifyOtp(AuthByAbdmRequest authByAbdmRequest) {
        return null;
    }
}