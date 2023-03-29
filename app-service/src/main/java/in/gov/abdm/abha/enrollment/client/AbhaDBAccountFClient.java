package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name= AbhaConstants.ABHA_DB_ACCOUNT_CLIENT, url="${enrollment.gateway.enrollmentdb.baseuri}", configuration = BeanConfiguration.class)
public interface AbhaDBAccountFClient {

    @GetMapping(URIConstant.FDB_GET_ACCOUNT_BY_XML_UID)
    public Mono<AccountDto> getAccountByXmlUid(@PathVariable("xmlUid") String xmlUid);

    @GetMapping(URIConstant.FDB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public Mono<AccountDto> getAccountByHealthIdNumber(@PathVariable("healthIdNumber")String healthIdNumber);

    @PostMapping(URIConstant.DB_ADD_ACCOUNT_URI)
    public Mono<AccountDto> createAccount(@RequestBody AccountDto accountDto);

    @PatchMapping(URIConstant.DB_UPDATE_ACCOUNT_URI)
    public Mono<AccountDto> updateAccount(@RequestBody AccountDto accountDto,@PathVariable("id") String healthIdNumber);

    @GetMapping(URIConstant.FDB_GET_ACCOUNTS_BY_HEALTH_ID_NUMBER)
    public Flux<AccountDto> getAccountsByHealthIdNumbers(@PathVariable("healthIdNumber") String healthIdNumber);

    @GetMapping(URIConstant.FDB_GET_ACCOUNT_BY_DOCUMENT_CODE)
    public Mono<AccountDto> getAccountEntityByDocumentCode(@PathVariable("documentCode") String documentCode);

    @GetMapping(URIConstant.GET_LINKED_ACCOUNT_COUNT_BY_MOBILE_NUMBER)
    public Mono<Integer> getMobileLinkedAccountCount(@PathVariable("mobileNumber") String mobileNumber);

    @GetMapping(URIConstant.GET_LINKED_ACCOUNT_COUNT_BY_EMAIL)
    public Mono<Integer> getEmailLinkedAccountCount(@PathVariable("email") String email);
}
