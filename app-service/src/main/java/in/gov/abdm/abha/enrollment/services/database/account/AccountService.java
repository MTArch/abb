package in.gov.abdm.abha.enrollment.services.database.account;

import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidReattemptDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountService {

    Mono<AccountDto> findByXmlUid(String xmlUid);

    Mono<AccountDto> prepareNewAccount(TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, List<LgdDistrictResponse> lgdDistrictResponse);

    Mono<AccountDto> createAccountEntity(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto , AccountDto accountDto, RequestHeaders requestHeaders);

    boolean isItNewUser(AccountDto accountDto);

    Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber);

    Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber);

    Mono<AccountDto> getAccountByDocumentCode(String documentChecksum);

    Flux<AccountDto> getAccountsByHealthIdNumbers(List<String> healthIdNumbers);

    Mono<Integer> getMobileLinkedAccountCount(@PathVariable("mobileNumber") String mobileNumber);

    Mono<Integer> getEmailLinkedAccountCount(@PathVariable("email") String email);
    Mono<String> saveAllData(SaveAllDataRequest saveAllDataRequest);

    Mono<AccountDto> settingClientIdAndOrigin(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AccountDto accountDto, RequestHeaders requestHeaders);
    void mapAccountWithEkyc(AadhaarResponseDto aadhaarResponseDto,AccountDto accountDto,List<LgdDistrictResponse> lgdDistrictResponse);
    
    Mono<Void> reAttemptedAbha(String aNumber, String rType , RequestHeaders rHeaders);
    
    
}
