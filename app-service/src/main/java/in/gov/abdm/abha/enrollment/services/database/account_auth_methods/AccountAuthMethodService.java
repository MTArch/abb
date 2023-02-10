package in.gov.abdm.abha.enrollment.services.database.account_auth_methods;

import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountAuthMethodService {
    Mono<List<AccountAuthMethodsDto>> addAccountAuthMethods(List<AccountAuthMethodsDto> authMethodsDtos);
}
