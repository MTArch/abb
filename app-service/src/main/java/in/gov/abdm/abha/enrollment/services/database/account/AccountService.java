package in.gov.abdm.abha.enrollment.services.database.account;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountService {

    Mono<AccountDto> findByXmlUid(String xmlUid);

    Mono<AccountDto> prepareNewAccount(TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, List<LgdDistrictResponse> lgdDistrictResponse);

    Mono<AccountDto> createAccountEntity(AccountDto accountDto);

    boolean isItNewUser(AccountDto accountDto);

    Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber);

    Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber);

}
