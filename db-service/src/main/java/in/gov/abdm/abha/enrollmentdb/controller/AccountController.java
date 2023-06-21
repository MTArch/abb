package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.account.AccountService;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.de_duplication.DeDuplicationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Base64;
import java.util.List;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_ACCOUNTS;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_LOG_MSG;

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
    public ResponseEntity<?> getAccountByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on healthIdNumber= "+healthIdNumber+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountByHealthIdNumber(healthIdNumber)
                .switchIfEmpty(Mono.empty()));
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountDto accounts) {
        log.info(ENROLLMENT_DB_LOG_MSG+"save data= "+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.addAccount(accounts));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<?> updateAccount(@RequestBody AccountDto accountDto,
                                           @PathVariable("healthIdNumber") String healthIdNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG+"update data based on healthIdNumber= "+healthIdNumber+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.updateAccountByHealthIdNumber(accountDto, healthIdNumber));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNT_BY_XML_UID)
    public ResponseEntity<?> getAccountByXmlUid(@PathVariable("xmluid") String xmluid) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on xmluid= "+xmluid+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountByXmlUid(new String(Base64.getDecoder().decode(xmluid))).publishOn(Schedulers.boundedElastic()));
    }

    @GetMapping
    public ResponseEntity<?> getAccountsByHealthIdNumbers(@RequestParam("healthIdNumber") List<String> healthIdNumbers) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on healthIdNumber list= "+healthIdNumbers+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountsByHealthIdNumbers(healthIdNumbers));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNT_BY_DOCUMENT_CODE)
    public ResponseEntity<?> getAccountByDocumentCode(@PathVariable("documentCode") String documentCode) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on documentCode= "+documentCode+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getAccountByDocumentCode(documentCode));
    }

    @GetMapping(ABHAEnrollmentDBConstant.GET_LINKED_ACCOUNT_COUNT_BY_MOBILE_NUMBER)
    public ResponseEntity<?> getMobileLinkedAccountCount(@PathVariable("mobileNumber") String mobileNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get count of data based on mobileNumber= "+mobileNumber+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getMobileLinkedAccountsCount(mobileNumber).publishOn(Schedulers.boundedElastic()));
    }

    @GetMapping(ABHAEnrollmentDBConstant.GET_LINKED_ACCOUNT_COUNT_BY_EMAIL)
    public ResponseEntity<?> getEmailLinkedAccountCount(@PathVariable("email") String email) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get count of data based on email= "+email+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.getEmailLinkedAccountsCount(email));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.DB_GET_DUPLICATE_ACCOUNT)
    public ResponseEntity<?> checkDeDuplication(@RequestBody DeDuplicationRequest request) {
        log.info(ENROLLMENT_DB_LOG_MSG+"check de-duplication= "+ENROLLMENT_DB_ACCOUNTS);
        return ResponseEntity.ok(accountService.checkDeDuplication(request));
    }
}