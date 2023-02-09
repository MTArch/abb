package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import org.springframework.web.bind.annotation.PostMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.List;
@ReactiveFeignClient(name= AbhaConstants.ABHA_DB_ACCOUNT_AUTH_METHODS_CLIENT, url="${enrollment.gateway.enrollmentdb.baseuri}", configuration = BeanConfiguration.class)
public interface AbhaDBAccountAuthMethodsFClient {

    @PostMapping(URIConstant.DB_ADD_ACCOUNT_AUTH_METHODS_ENDPOINT)
    public Mono<List<AccountAuthMethodsDto>> addAccountAuthMethods(List<AccountAuthMethodsDto> authMethodsDtos);
}
