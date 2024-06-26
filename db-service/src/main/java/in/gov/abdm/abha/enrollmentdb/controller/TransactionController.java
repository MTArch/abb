package in.gov.abdm.abha.enrollmentdb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.transaction.TransactionService;
import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping(ABHAEnrollmentDBConstant.TRANSACTION_ENDPOINT)
@Slf4j
public class TransactionController {

    public static final String SEARCHING_FOR_TRANSACTION = "searching for transaction : ";
    public static final String CREATING_NEW_TRANSACTION = "creating new transaction : ";
    public static final String DELETE_TRANSACTION_BY_TXN_ID = "Delete Transaction by Txn id :";
    public static final String UPDATING_TRANSACTION = "updating transaction : ";

    @Autowired
    TransactionService transactionService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_TRANSACTION_BY_TXN_ID)
    public ResponseEntity<Mono<TransactionDto>> getTransactionByTxnId(@PathVariable("txnId") String txnId) {
        log.info(SEARCHING_FOR_TRANSACTION +txnId);
        return ResponseEntity.ok(transactionService.getTransactionByTxnId(txnId).publishOn(Schedulers.boundedElastic()));
    }

    @PostMapping
    public ResponseEntity<Mono<TransactionDto>> createTransaction(@RequestBody TransactionDto transactionDto) {
        log.info(CREATING_NEW_TRANSACTION +transactionDto.getTxnId());
        return ResponseEntity.ok(transactionService.createTransaction(transactionDto));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_TRANSACTION_BY_ID)
    public ResponseEntity<Mono<TransactionDto>> updateTransactionById(@RequestBody TransactionDto transactionDto, @PathVariable("id") String id) {
    	log.info(UPDATING_TRANSACTION +transactionDto.getTxnId());
    	return ResponseEntity.ok(transactionService.updateTransactionById(transactionDto, id));
    }

    @DeleteMapping(ABHAEnrollmentDBConstant.DELETE_TRANSACTION_BY_TXN_ID)
    public Mono<Void> deleteTransactionByTxnId(@PathVariable("txnId") String txnId){
        log.info(DELETE_TRANSACTION_BY_TXN_ID +txnId);
        return transactionService.deleteTransactionByTxnId(txnId);
    }
}
