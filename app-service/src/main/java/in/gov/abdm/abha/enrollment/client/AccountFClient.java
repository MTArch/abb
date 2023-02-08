package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@ReactiveFeignClient(name="account-enrolment-db-client", url="${enrollment.gateway.enrollmentdb.baseuri}", configuration = BeanConfiguration.class)
public interface AccountFClient {

    @GetMapping(URIConstant.DB_GET_ACCOUNT_BY_XML_UID+"{xmlUid}")
    public Mono<AccountDto> getAccountByXmlUid(@PathVariable("xmlUid") String xmlUid);

    @GetMapping(URIConstant.DB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER+"{healthIdNumber}")
    public Mono<AccountDto> getAccountByHealthIdNumber(@PathVariable("healthIdNumber")String healthIdNumber);

    @PostMapping(URIConstant.DB_ADD_ACCOUNT_URI)
    public Mono<AccountDto> createAccount(@RequestBody AccountDto accountDto);

    @PatchMapping(URIConstant.DB_UPDATE_ACCOUNT_URI)
    public Mono<AccountDto> updateAccount(@RequestBody AccountDto accountDto,@PathVariable("id") String healthIdNumber);

    @GetMapping(URIConstant.DB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER+"{healthIdNumber}")
    public Flux<AccountDto> getAccountsByHealthIdNumbers(@PathVariable("healthIdNumber") String healthIdNumber);

    @GetMapping(URIConstant.DB_GET_ACCOUNT_BY_DOCUMENT_CODE)
    public Mono<AccountDto> getAccountEntityByDocumentCode(@PathVariable("documentCode") String documentCode);
}
