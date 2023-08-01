package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.accountaction.AccountActionService;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_ACCOUNT_ACTION;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_LOG_MSG;

@RequestMapping(ABHAEnrollmentDBConstant.ACCOUNT_ACTION_ENDPOINT)
@Slf4j
@RestController
public class AccountActionController {
    @Autowired
    AccountActionService accountActionService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<Mono<AccountActionDto>> getAccountByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on HealthIdNumber= "+healthIdNumber+ENROLLMENT_DB_ACCOUNT_ACTION);
        return ResponseEntity.ok(accountActionService.getAccountActionByHealthIdNumber(healthIdNumber)
                .switchIfEmpty(Mono.empty()));
    }

    @PostMapping
    public ResponseEntity<Mono<AccountActions>> saveAccountAction(@RequestBody AccountActions accountAction) {
        log.info(ENROLLMENT_DB_LOG_MSG+"save data "+ENROLLMENT_DB_ACCOUNT_ACTION);
        return ResponseEntity.ok(accountActionService.addAccount(accountAction).switchIfEmpty(Mono.empty()));
    }
}
