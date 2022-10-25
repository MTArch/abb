package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.account.AccountService;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@RequestMapping(ABHAEnrollmentDBConstant.ACCOUNT_ENDPOINT)
@RestController
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<?> getAccountByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
        return ResponseEntity.ok(accountService.getAccountByHealthIdNumber(healthIdNumber));
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountDto accounts) {
        return ResponseEntity.ok(accountService.addAccount(accounts));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_ACCOUNT_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<?> updateAccount(@RequestBody AccountDto accountDto,
                                           @PathVariable("healthIdNumber") String healthIdNumber) {
        return ResponseEntity.ok(accountService.updateAccountByHealthIdNumber(accountDto, healthIdNumber));
    }

}
