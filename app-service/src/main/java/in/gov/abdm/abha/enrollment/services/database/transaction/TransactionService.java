package in.gov.abdm.abha.enrollment.services.database.transaction;

import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import reactor.core.publisher.Mono;

public interface TransactionService {

    void mapTransactionWithEkyc(TransactionDto transactionDto, AadhaarUserKycDto kycData, String kycType);

    String generateTransactionId(boolean isKYCTxn);

    Mono<TransactionDto> createTransactionEntity(TransactionDto transactionDto);

    Mono<TransactionDto> findTransactionDetailsFromDB(String txnId);

    Mono<TransactionDto> updateTransactionEntity(TransactionDto transactionDto, String transactionId);
}
