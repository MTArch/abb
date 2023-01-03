package in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods;


import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AccountAuthMethodsService {
    Flux addAccountAuthMethods(List<AccountAuthMethods> accountAuthMethods);
}
