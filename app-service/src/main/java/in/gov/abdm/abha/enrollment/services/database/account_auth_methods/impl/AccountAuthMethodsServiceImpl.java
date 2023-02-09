package in.gov.abdm.abha.enrollment.services.database.account_auth_methods.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountAuthMethodsFClient;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class AccountAuthMethodsServiceImpl implements AccountAuthMethodService {

    @Autowired
    AbhaDBAccountAuthMethodsFClient abhaDBAccountAuthMethodsFClient;

    @Override
    public Mono<List<AccountAuthMethodsDto>> addAccountAuthMethods(List<AccountAuthMethodsDto> authMethodsDtos) {
        return abhaDBAccountAuthMethodsFClient.addAccountAuthMethods(authMethodsDtos)
                .onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
    }
}
