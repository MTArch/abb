package in.gov.abdm.abha.enrollment.services.database.accountaction;


import in.gov.abdm.abha.enrollment.model.entities.AccountActionDto;

import reactor.core.publisher.Mono;

public interface AccountActionService {

    Mono<AccountActionDto> createAccountActionEntity(AccountActionDto accountActionDto);
}
