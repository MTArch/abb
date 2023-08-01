package in.gov.abdm.abha.enrollmentdb.controller;


import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods.AccountAuthMethodsService;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_AUTH_METHODS;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_LOG_MSG;


@RequestMapping(ABHAEnrollmentDBConstant.ACCOUNT_AUTH_METHODS_ENDPOINT)
@Slf4j
@RestController
public class AccountAuthMethodsController {

    @Autowired
    AccountAuthMethodsService accountAuthMethodsService;


    @PostMapping
    public ResponseEntity<Flux<AccountAuthMethodsDto>> createAuthMethods(@RequestBody List<AccountAuthMethods> accountAuthMethods) {
        log.info(ENROLLMENT_DB_LOG_MSG+"save data "+ENROLLMENT_DB_AUTH_METHODS);
        return ResponseEntity.ok(accountAuthMethodsService.addAccountAuthMethods(accountAuthMethods));
    }

    @DeleteMapping(ABHAEnrollmentDBConstant.DELETE_ACCOUNT_AUTH_METHOD_BY_HEALTH_ID)
    public Mono<Void> deleteAccountAuthByHealthId(@PathVariable("healthIdNumber") String healthIdNumber){
        log.info(ENROLLMENT_DB_LOG_MSG+"delete data based on abhaNumber= "+healthIdNumber+ENROLLMENT_DB_AUTH_METHODS);
        return accountAuthMethodsService.deleteAccountAuthMethodsByHealthId(healthIdNumber);
    }
}

