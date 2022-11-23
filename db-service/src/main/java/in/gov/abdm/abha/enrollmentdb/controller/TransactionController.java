package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.transaction.TransactionService;
import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ABHAEnrollmentDBConstant.TRANSACTION_ENDPOINT)
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_TRANSACTION_BY_TXN_ID)
    public Mono<TransactionDto> getTransactionByTxnId(@PathVariable("txnId") String txnId) {
        return transactionService.getTransactionByTxnId(txnId);
    }

    @PostMapping
    public Mono<Transection> createTransaction(@RequestBody TransactionDto transactionDto) {
        return transactionService.createTransaction(transactionDto);
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_TRANSACTION_BY_ID)
    public Mono<Transection> updateTransactionById(@RequestBody TransactionDto transactionDto, @PathVariable("id") String id) {
        return transactionService.updateTransactionById(transactionDto, id);
    }
}
