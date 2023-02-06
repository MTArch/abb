package in.gov.abdm.abha.enrollmentdb.domain.accountaction;

import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import reactor.core.publisher.Mono;

/**
 * Service layer to perform crud operations on AccountAction Entity
 */
public interface AccountActionService {
    Mono<AccountActions> updateAccountActionByHealthIdNumber(AccountActionDto accountActionDto, String healthIdNumber);

    Mono<AccountActions> getAccountActionByHealthIdNumber(String healthIdNumber);

    Mono<AccountActionDto> addAccount(AccountActionDto accountActionDto);
}
