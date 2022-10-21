package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.template.TemplateDTO;
import in.gov.abdm.abha.enrollmentdb.domain.transaction.TransactionDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@RestController
@RequestMapping(ABHAEnrollmentDBConstant.API_VERSION)
public class ABHAEnrollmentDBController {

    @GetMapping(ABHAEnrollmentDBConstant.FIND_TEMPLATE_TRANSACTION)
    public Mono<TemplateDTO> tempTransaction(@PathVariable String name) {
        return Mono.empty();
    }

    @PostMapping(ABHAEnrollmentDBConstant.TEMPLATE_TRANSACTION)
    public Mono tempTransaction(@RequestBody TemplateDTO templateDTO) {
        return Mono.empty();
    }

    @PutMapping(ABHAEnrollmentDBConstant.TEMPLATE_TRANSACTION)
    public Mono<TemplateDTO> tempTransaction(@PathVariable BigInteger id, @RequestBody TemplateDTO templateDTO) {
        return Mono.empty();
    }

    @DeleteMapping(ABHAEnrollmentDBConstant.TEMPLATE_TRANSACTION)
    public Mono<TemplateDTO> tempTransaction(@PathVariable BigInteger id) {
        return Mono.empty();
    }

    @GetMapping(ABHAEnrollmentDBConstant.FIND_TRANSACTION_TRANSACTION)
    public Mono<TransactionDTO> txnTransaction(@PathVariable String name) {
        return Mono.empty();
    }

    @PostMapping(ABHAEnrollmentDBConstant.TRANSACTION_TRANSACTION)
    public Mono txnTransaction(@RequestBody TransactionDTO transactionDTO) {
        return Mono.empty();
    }

    @PutMapping(ABHAEnrollmentDBConstant.TRANSACTION_TRANSACTION)
    public Mono<TransactionDTO> txnTransaction(@PathVariable BigInteger id, @RequestBody TransactionDTO transactionDTO) {
        return Mono.empty();
    }

    @DeleteMapping(ABHAEnrollmentDBConstant.TRANSACTION_TRANSACTION)
    public Mono<TransactionDTO> txnTransaction(@PathVariable BigInteger id) {
        return Mono.empty();
    }
}
