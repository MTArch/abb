package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.AccountActionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name= AbhaConstants.ABHA_DB_ACCOUNT_ACTION_CLIENT, url="${enrollment.gateway.enrollmentdb.baseuri}", configuration = BeanConfiguration.class)
public interface AbhaDBAccountActionFClient {
    @GetMapping(URIConstant.DB_GET_ACCOUNT_ACTION_BY_HEALTH_ID_NUMBER)
    Mono<AccountActionDto> getAccountActionByHealthId(@PathVariable("id") String healthIdNumber);

    @PostMapping(URIConstant.DB_ADD_ACCOUNT_ACTION_URI)
    Mono<AccountActionDto> postAccountAction(@RequestBody AccountActionDto accountActionDto);
}