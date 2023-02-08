package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name="transaction-enrolment-db-client", url="${enrollment.gateway.enrollmentdb.baseuri}", configuration = BeanConfiguration.class)
public interface TransactionFClient {

    @GetMapping(URIConstant.DB_GET_TRANSACTION_BY_TXN_ID+"{txnId}")
    public Mono<TransactionDto> getTransactionByTxnId(@PathVariable("txnId") String txnId);

    @PostMapping(URIConstant.DB_ADD_TRANSACTION_URI)
    public Mono<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto);

    @PatchMapping(URIConstant.DB_UPDATE_TRANSACTION_URI)
    public Mono<TransactionDto>updateTransactionById(@RequestBody TransactionDto transactionDto, @PathVariable("id") String transactionId);

    @DeleteMapping(URIConstant.DB_DELETE_TRANSACTION_URI)
    public Mono<ResponseEntity<Mono<Void>> > deleteTransactionByTxnId(@PathVariable("txnId") String txnId);
}
