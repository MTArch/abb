package in.gov.abdm.abha.enrollment.services.database.account;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import reactor.core.publisher.Mono;

public interface AccountService {

    AccountDto findByXmlUid(String xmlUid);

    AccountDto prepareNewAccount(TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto);

    Mono<AccountDto> createAccountEntity(AccountDto accountDto);

    boolean isItNewUser(AccountDto accountDto);
}