package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.transaction.TransactionService;
import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ABHAEnrollmentDBConstant.TRANSACTION_ENDPOINT)
@Slf4j
public class TransactionController {

    public static final String SEARCHING_FOR_TRANSACTION = "searching for transaction : ";
    public static final String CREATING_NEW_TRANSACTION = "creating new transaction : ";
    public static final String DELETE_TRANSACTION_BY_TXN_ID = "Delete Transaction by Txn id :";
    @Autowired
    TransactionService transactionService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_TRANSACTION_BY_TXN_ID)
    public Mono<TransactionDto> getTransactionByTxnId(@PathVariable("txnId") String txnId) {
        log.info(SEARCHING_FOR_TRANSACTION +txnId);
        return transactionService.getTransactionByTxnId(txnId);
    }

    @PostMapping
    public Mono<Transection> createTransaction(@RequestBody TransactionDto transactionDto) {
        log.info(CREATING_NEW_TRANSACTION +transactionDto.getTxnId());
        return transactionService.createTransaction(transactionDto);
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_TRANSACTION_BY_ID)
    public Mono<Transection> updateTransactionById(@RequestBody TransactionDto transactionDto, @PathVariable("id") String id) {
        return transactionService.updateTransactionById(transactionDto, id);
    }

    @DeleteMapping(ABHAEnrollmentDBConstant.DELETE_TRANSACTION_BY_TXN_ID)
    public Mono<Void> deleteTransactionByTxnId(@PathVariable("txnId") String txnId){
        log.info(DELETE_TRANSACTION_BY_TXN_ID +txnId);
        return transactionService.deleteTransactionByTxnId(txnId);
    }
}
