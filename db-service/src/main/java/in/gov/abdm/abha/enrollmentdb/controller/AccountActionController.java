package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.accountaction.AccountActionService;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping(ABHAEnrollmentDBConstant.ACCOUNT_ACTION_ENDPOINT)
@RestController
public class AccountActionController {
    @Autowired
    AccountActionService accountActionService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<?> getAccountByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
        return ResponseEntity.ok(accountActionService.getAccountActionByHealthIdNumber(healthIdNumber)
                .switchIfEmpty(Mono.empty()));
    }

    @PostMapping
    public ResponseEntity<?> saveAccountAction(@RequestBody AccountActions accountAction) {
        return ResponseEntity.ok(accountActionService.addAccount(accountAction).switchIfEmpty(Mono.empty()));
    }
}
