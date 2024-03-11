package in.gov.abdm.abha.enrollmentdb.controller;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_ACCOUNTS;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_LOG_MSG;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.account.AccountService;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.abha.enrollmentdb.model.de_duplication.DeDuplicationRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 *
 */
@RequestMapping(ABHAEnrollmentDBConstant.ACCOUNT_ENDPOINT)
@Slf4j
@RestController
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<Mono<AccountDto>> getAccountByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on healthIdNumber= "+healthIdNumber+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountByHealthIdNumber(healthIdNumber)
                .switchIfEmpty(Mono.empty()));
    }

    @PostMapping
    public ResponseEntity<Mono<AccountDto>> createAccount(@RequestBody AccountDto accounts) {
        log.info(ENROLLMENT_DB_LOG_MSG+"save data= "+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.addAccount(accounts));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<Mono<AccountDto>> updateAccount(@RequestBody AccountDto accountDto,
                                           @PathVariable("healthIdNumber") String healthIdNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG+"update data based on healthIdNumber= "+healthIdNumber+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.updateAccountByHealthIdNumber(accountDto, healthIdNumber));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNT_BY_XML_UID)
    public ResponseEntity<Mono<AccountDto>> getAccountByXmlUid(@PathVariable("xmluid") String xmluid) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on xmluid= "+xmluid+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountByXmlUid(new String(Base64.getDecoder().decode(xmluid))).publishOn(Schedulers.boundedElastic()));
    }

    @GetMapping
    public ResponseEntity<Flux<AccountDto>> getAccountsByHealthIdNumbers(@RequestParam("healthIdNumber") List<String> healthIdNumbers) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on healthIdNumber list= "+healthIdNumbers+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountsByHealthIdNumbers(healthIdNumbers));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNT_BY_DOCUMENT_CODE)
    public ResponseEntity<Mono<AccountDto>> getAccountByDocumentCode(@PathVariable("documentCode") String documentCode) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on documentCode= "+documentCode+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountByDocumentCode(documentCode));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNTS_BY_DOCUMENT_CODE)
    public ResponseEntity<Flux<AccountDto>> getAccountsByDocumentCode(@PathVariable("documentCode") String documentCode) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on documentCode= "+documentCode+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountsByDocumentCode(documentCode));
    }

    @GetMapping(ABHAEnrollmentDBConstant.GET_LINKED_ACCOUNT_COUNT_BY_MOBILE_NUMBER)
    public ResponseEntity<Mono<Integer>> getMobileLinkedAccountCount(@PathVariable("mobileNumber") String mobileNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get count of data based on mobileNumber= "+mobileNumber+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getMobileLinkedAccountsCount(mobileNumber).publishOn(Schedulers.boundedElastic()));
    }

    @GetMapping(ABHAEnrollmentDBConstant.GET_LINKED_ACCOUNT_COUNT_BY_EMAIL)
    public ResponseEntity<Mono<Integer>> getEmailLinkedAccountCount(@PathVariable("email") String email) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get count of data based on email= "+email+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getEmailLinkedAccountsCount(email));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.DB_GET_DUPLICATE_ACCOUNT)
    public ResponseEntity<Mono<AccountDto>> checkDeDuplication(@RequestBody DeDuplicationRequest request) {
        log.info(ENROLLMENT_DB_LOG_MSG+"check de-duplication= "+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.checkDeDuplication(request));
    }
    
	@PostMapping(value = ABHAEnrollmentDBConstant.ACCOUNT_REATTEMPT_ENDPOINT)
	public ResponseEntity<Mono<Void>> sendReattemptAbha(@RequestBody AccountReattemptDto aReattemptDto) {
		log.info(ENROLLMENT_DB_LOG_MSG + "send data to kafka= " + ENROLLMENT_DB_ACCOUNTS);
		return ResponseEntity.ok(accountService.sendAbhaToKafka(aReattemptDto));
	}
}