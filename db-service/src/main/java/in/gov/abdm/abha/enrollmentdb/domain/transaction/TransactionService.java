package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<TransactionDto> createTransaction(TransactionDto transactionDto);

    Mono<TransactionDto> getTransaction(Long var1);

    Mono<TransactionDto> getTransactionByTxnId(String txnId);

    Mono<TransactionDto> updateTransactionById(TransactionDto transactionDto, String id);

    Mono<Void> deleteTransactionByTxnId(String txnId);
}