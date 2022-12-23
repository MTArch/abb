package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Transection> createTransaction(TransactionDto var1);

    Mono<TransactionDto> getTransaction(Long var1);

    Mono<TransactionDto> getTransactionByTxnId(String txnId);

    Mono<Transection> updateTransactionById(TransactionDto var1, String var2);

    Mono<Void> deleteTransactionByTxnId(String txnId);
}