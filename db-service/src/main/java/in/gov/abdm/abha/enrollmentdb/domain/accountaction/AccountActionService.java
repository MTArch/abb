package in.gov.abdm.abha.enrollmentdb.domain.accountaction;

import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import reactor.core.publisher.Mono;

/**
 * Service layer to perform crud operations on AccountAction Entity
 */
public interface AccountActionService {

    Mono<AccountActionDto> getAccountActionByHealthIdNumber(String healthIdNumber);

    Mono<AccountActions> addAccount(AccountActions accountActions);
}
