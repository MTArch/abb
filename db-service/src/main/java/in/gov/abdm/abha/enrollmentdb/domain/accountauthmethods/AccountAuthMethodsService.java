package in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods;


import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountAuthMethodsService {
    Flux<AccountAuthMethodsDto> addAccountAuthMethods(List<AccountAuthMethods> accountAuthMethods);

    Mono<Void> deleteAccountAuthMethodsByHealthId(String healthIdNumber);
}
