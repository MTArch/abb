package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl;

import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
// JUNIT Test cases to test EnrolUsingAadhaarServiceImpl class
public class EnrolUsingAadhaarServiceImplTest {

    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @MockBean
    AadhaarClient aadhaarClient;
    EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
    AadhaarResponseDto aadhaarResponseDto = new AadhaarResponseDto();
    TransactionDto transactionDto = new TransactionDto();
    private WebTestClient webTestClient;
    @Mock
    private EnrolUsingAadhaarServiceImpl enrolUsingAadhaarServiceimpl;

    //Test case to check Status based on age
    @Test
    void checkingStatusBasedOnAge() {

/**
         try{

         // EnrolUsingAadhaarServiceImpl enrolUsingAadhaarServiceImpl;
             AccountDto accountDto = new AccountDto();

         when(enrolUsingAadhaarServiceimpl.HandleAadhaarOtpResponse(enrolByAadhaarRequestDto,aadhaarResponseDto,transactionDto)).thenReturn(accountDto.get);

         transactionDto.setYearOfBirth("18");
         // Assert.assertEquals(enrolUsingAadhaarServiceimpl.HandleAadhaarOtpResponse(enrolByAadhaarRequestDto,aadhaarResponseDto,transactionDto).,);
         Assert.assertEquals(transactionDto.getStatus(),"STANDARD");


         }catch (Exception e){
         e.printStackTrace();
         }
         */
    }
    //

    @Test
    void checkingEmptyField() {
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        AadhaarResponseDto aadhaarResponseDto = new AadhaarResponseDto();
        TransactionDto transactionDto = new TransactionDto();

        try {

            // EnrolUsingAadhaarServiceImpl enrolUsingAadhaarServiceImpl;
            //when(enrolUsingAadhaarServiceimpl.HandleAadhaarOtpResponse(enrolByAadhaarRequestDto, aadhaarResponseDto, transactionDto)).thenReturn();

            transactionDto.setYearOfBirth("18");


            //test the HandleAadhaarOtpResponse functionality
            // Assert.assertEquals(enrolUsingAadhaarServiceimpl.HandleAadhaarOtpResponse(enrolByAadhaarRequestDto,aadhaarResponseDto,transactionDto),);
            // Assert.assertEquals(enrolUsingAadhaarServiceimpl.HandleAadhaarOtpResponse(enrolByAadhaarRequestDto,aadhaarResponseDto,transactionDto).,);
            Assert.assertEquals("Enter the value", transactionDto.getStatus(), "STANDARD");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

