package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.de_duplication.DeDuplicationRequest;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidReattemptDto;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI;

@ReactiveFeignClient(name = AbhaConstants.ABHA_DB_ACCOUNT_CLIENT, url = ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI, configuration = BeanConfiguration.class)
public interface AbhaDBAccountFClient {

    @GetMapping(URIConstant.FDB_GET_ACCOUNT_BY_XML_UID)
    public Mono<AccountDto> getAccountByXmlUid(@PathVariable("xmlUid") String xmlUid);

    @GetMapping(URIConstant.FDB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public Mono<AccountDto> getAccountByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber);

    @PostMapping(URIConstant.DB_ADD_ACCOUNT_URI)
    public Mono<AccountDto> createAccount(@RequestBody AccountDto accountDto);

    @PatchMapping(URIConstant.DB_UPDATE_ACCOUNT_URI)
    public Mono<AccountDto> updateAccount(@RequestBody AccountDto accountDto, @PathVariable("id") String healthIdNumber);

    @GetMapping(URIConstant.FDB_GET_ACCOUNTS_BY_HEALTH_ID_NUMBER)
    public Flux<AccountDto> getAccountsByHealthIdNumbers(@PathVariable("healthIdNumber") String healthIdNumber);

    @GetMapping(URIConstant.FDB_GET_ACCOUNT_BY_DOCUMENT_CODE)
    public Mono<AccountDto> getAccountEntityByDocumentCode(@PathVariable("documentCode") String documentCode);

    @GetMapping(URIConstant.FDB_GET_ACCOUNTS_BY_DOCUMENT_CODE)
    public Flux<AccountDto> getAccountsEntityByDocumentCode(@PathVariable("documentCode") String documentCode);

    @GetMapping(URIConstant.GET_ACCOUNTS_BY_DOCUMENT_CODE_ENROL)
    public Flux<AccountDto> getAccountsEntityByDocumentCodeEnrol(@PathVariable("documentCode") String documentCode);

    @GetMapping(URIConstant.GET_LINKED_ACCOUNT_COUNT_BY_MOBILE_NUMBER)
    public Mono<Integer> getMobileLinkedAccountCount(@PathVariable("mobileNumber") String mobileNumber);

    @GetMapping(URIConstant.GET_LINKED_ACCOUNT_COUNT_BY_EMAIL)
    public Mono<Integer> getEmailLinkedAccountCount(@PathVariable("email") String email);

    @GetMapping(URIConstant.FDB_GET_DUPLICATE_ACCOUNT)
    Mono<AccountDto> checkDeDuplication(@RequestBody DeDuplicationRequest request);

    @PostMapping(URIConstant.ACCOUNT_SAVE_ALL)
    Mono<String> saveAllData(@RequestBody SaveAllDataRequest saveAllDataRequest);

    @PostMapping(URIConstant.ABHA_REATTEMPTED)
    Mono<Void> reAttemptedAbha(@RequestBody HidReattemptDto hidReattemptDto);

}
