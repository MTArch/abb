package in.gov.abdm.abha.enrollment.services.database.accountaction;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountActionDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountActionService {


    Mono<AccountActionDto> getAccountActionByHealthIdNumber(String healthIdNumber);

    Mono<AccountActionDto> createAccountActionEntity(AccountActionDto accountActionDto);
}
