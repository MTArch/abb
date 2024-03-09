package in.gov.abdm.abha.enrollment.services.database;

import in.gov.abdm.abha.enrollment.client.AbhaDBTransactionFClient;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.transaction.impl.TransactionServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class TransactionServiceImplTests {

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Mock
    AbhaDBTransactionFClient abhaDBTransactionFClient;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void createHidPhrAddressEntityTest(){
        TransactionDto transactionDto = new TransactionDto();
        AadhaarUserKycDto aadhaarUserKycDto = new AadhaarUserKycDto();
        transactionDto.setHouse("house");
        transactionDto.setLm("lm");
        transactionDto.setLoc("loc");
        transactionDto.setVillageName("village name");

        aadhaarUserKycDto.setPhoto("Photo");
        aadhaarUserKycDto.setBirthdate("10-10-2000");
        aadhaarUserKycDto.setEmail("email");
        aadhaarUserKycDto.setPincode("123564");
        aadhaarUserKycDto.setName("name");
        aadhaarUserKycDto.setGender("Male");
        aadhaarUserKycDto.setVillageTownCity("villagetowncity");
        aadhaarUserKycDto.setSignature("sign");
        aadhaarUserKycDto.setReason("reason");
        aadhaarUserKycDto.setStatus("Success");
        aadhaarUserKycDto.setResponseCode("response code");
        aadhaarUserKycDto.setCareOf("careof");
        aadhaarUserKycDto.setHouse("house");
        aadhaarUserKycDto.setState("state");
        aadhaarUserKycDto.setLandmark("landmark");
        aadhaarUserKycDto.setLocality("locality");
        aadhaarUserKycDto.setStreet("street");
        aadhaarUserKycDto.setVillageTownCity("village");
        aadhaarUserKycDto.setSubDist("subdist");
        aadhaarUserKycDto.setDistrict("district");

//        Mockito.when(abhaDBHidPhrAddressFClient.createHidPhrAddress(any())).thenReturn(Mono.just(hidPhrAddressDto));
        transactionService.mapTransactionWithEkyc(transactionDto,aadhaarUserKycDto,"asd");
        aadhaarUserKycDto.setBirthdate("2000");
        transactionService.mapTransactionWithEkyc(transactionDto,aadhaarUserKycDto,"asd");
        Assert.assertEquals("Failed to Validate","", "");

    }


    @Test
    public void generateTransactionIdTest(){
        String result =  transactionService.generateTransactionId(true);
        Assert.assertEquals("Failed to Validate","", "");

    }
    @Test
    public void createTransactionEntityTests(){
        TransactionDto transactionDto = new TransactionDto();
        Mockito.when(abhaDBTransactionFClient.createTransaction(any())).thenReturn(Mono.just(transactionDto));
        TransactionDto result =  transactionService.createTransactionEntity(transactionDto).block();
        Assert.assertEquals("Failed to Validate","","");
    }
    @Test
    public void findTransactionDetailsFromDBTests(){
        TransactionDto transactionDto = new TransactionDto();
        Mockito.when(abhaDBTransactionFClient.getTransactionByTxnId(any())).thenReturn(Mono.just(transactionDto));
        TransactionDto result =  transactionService.findTransactionDetailsFromDB("Test").block();
        Assert.assertEquals("Failed to Validate","","");
    }

    @Test
    public void updateTransactionEntityTests(){
        TransactionDto transactionDto = new TransactionDto();
        Mockito.when(abhaDBTransactionFClient.updateTransactionById(any(),any())).thenReturn(Mono.just(transactionDto));
        TransactionDto result =  transactionService.updateTransactionEntity(transactionDto,"Test").block();
        Assert.assertEquals("Failed to Validate","","");
    }
    @Test
    public void deleteTransactionEntityTests(){
        TransactionDto transactionDto = new TransactionDto();
        ResponseEntity<Mono<Void>> r = new ResponseEntity<>(HttpStatus.ACCEPTED);
        Mockito.when(abhaDBTransactionFClient.deleteTransactionByTxnId(any())).thenReturn(Mono.just(r));
        ResponseEntity<Mono<Void>> result =  transactionService.deleteTransactionEntity("Test").block();
        Assert.assertEquals("Failed to Validate","","");
    }


}
