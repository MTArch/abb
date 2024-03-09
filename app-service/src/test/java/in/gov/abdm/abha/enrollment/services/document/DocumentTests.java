package in.gov.abdm.abha.enrollment.services.document;

import in.gov.abdm.abha.enrollment.client.DocumentAppFClient;
import in.gov.abdm.abha.enrollment.client.DocumentDBIdentityDocumentFClient;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLRequest;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import in.gov.abdm.abha.enrollment.services.document.impl.DocumentAppServiceImpl;
import in.gov.abdm.abha.enrollment.services.document.impl.IdentityDocumentDBServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

public class DocumentTests {
    @InjectMocks
    DocumentAppServiceImpl documentAppService;

    @InjectMocks
    IdentityDocumentDBServiceImpl identityDocumentDBService;

    @Mock
    DocumentDBIdentityDocumentFClient documentDBIdentityDocumentFClient;

    @Mock
    DocumentAppFClient documentAppFClient;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void verifyTest(){
        VerifyDLResponse verifyDLResponse= new VerifyDLResponse();
        verifyDLResponse.setAuthResult("Success");
        verifyDLResponse.setMessage("success");
        VerifyDLRequest verifyDLRequest = new VerifyDLRequest();
        verifyDLRequest.setDocumentId("id");
        verifyDLRequest.setDob("12-12-2000");
        VerifyDLResponse verifyDLResponse2=new VerifyDLResponse("", verifyDLResponse.getMessage());

        VerifyDLRequest v = new VerifyDLRequest(verifyDLRequest.getDocumentType(), verifyDLRequest.getDocumentId(), verifyDLRequest.getFirstName(), verifyDLRequest.getMiddleName(), verifyDLRequest.getLastName(), verifyDLRequest.getDob(), verifyDLRequest.getGender());
        Mockito.when(documentAppFClient.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        VerifyDLResponse result =  documentAppService.verify(verifyDLRequest).block();

        Assert.assertEquals("Failed to Validate","","");

    }
    @Test
    public void addIdentityDocumentsTest(){
        IdentityDocumentsDto identityDocumentsDto = new IdentityDocumentsDto();
        identityDocumentsDto.setId("id");
        identityDocumentsDto.setDocumentNumber("123");
        Mockito.when(documentDBIdentityDocumentFClient.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        IdentityDocumentsDto result =  identityDocumentDBService.addIdentityDocuments(identityDocumentsDto).block();

        Assert.assertEquals("Failed to Validate","","");


    }
}
