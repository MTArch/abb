package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.TransactionController;
import in.gov.abdm.abha.enrollmentdb.domain.transaction.TransactionService;
import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class TransactionControllerTests {
    @InjectMocks
    TransactionController transactionController;
    @Mock
    TransactionService transactionService;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void getTransactionByTxnIdTests(){
        Mockito.when(transactionService.getTransactionByTxnId(any())).thenReturn(Mono.just(new TransactionDto()));
        ResponseEntity<Mono<TransactionDto>> response= transactionController.getTransactionByTxnId("test");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void createTransactionTests(){
        Mockito.when(transactionService.createTransaction(any())).thenReturn(Mono.just(new TransactionDto()));
        ResponseEntity<Mono<TransactionDto>> response= transactionController.createTransaction(new TransactionDto());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void updateTransactionByIdTests(){
        Mockito.when(transactionService.updateTransactionById(any(),any())).thenReturn(Mono.just(new TransactionDto()));
        ResponseEntity<Mono<TransactionDto>> response= transactionController.updateTransactionById(new TransactionDto(),"123");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void deleteTransactionByTxnIdTests(){
        Mockito.when(transactionService.deleteTransactionByTxnId(any())).thenReturn(Mono.empty());
        Mono<Void> response= transactionController.deleteTransactionByTxnId("test");
        //Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

}
