package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI;

@ReactiveFeignClient(name= AbhaConstants.ABHA_DB_TRANSACTION_CLIENT, url=ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI, configuration = BeanConfiguration.class)
public interface AbhaDBTransactionFClient {

    @GetMapping(URIConstant.FDB_GET_TRANSACTION_BY_TXN_ID)
    public Mono<TransactionDto> getTransactionByTxnId(@PathVariable("txnId") String txnId);

    @PostMapping(URIConstant.DB_ADD_TRANSACTION_URI)
    public Mono<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto);

    @PatchMapping(URIConstant.DB_UPDATE_TRANSACTION_URI)
    public Mono<TransactionDto>updateTransactionById(@RequestBody TransactionDto transactionDto, @PathVariable("id") String transactionId);

    @DeleteMapping(URIConstant.FDB_DELETE_TRANSACTION_URI)
    public Mono<ResponseEntity<Mono<Void>>> deleteTransactionByTxnId(@PathVariable("txnId") String txnId);
}
