package in.gov.abdm.abha.enrollmentdb.controller;


import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods.AccountAuthMethodsService;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping(ABHAEnrollmentDBConstant.ACCOUNT_AUTH_METHODS_ENDPOINT)
@RestController
public class AccountAuthMethodsController {

    @Autowired
    AccountAuthMethodsService accountAuthMethodsService;


    @PostMapping
    public ResponseEntity<?> createAuthMethods(@RequestBody List<AccountAuthMethods> accountAuthMethods) {
        return ResponseEntity.ok(accountAuthMethodsService.addAccountAuthMethods(accountAuthMethods));
    }
}

