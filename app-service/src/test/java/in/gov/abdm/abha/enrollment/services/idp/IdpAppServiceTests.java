package in.gov.abdm.abha.enrollment.services.idp;

import in.gov.abdm.abha.enrollment.client.IdpAppFClient;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.Kyc;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.Response;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.ErrorResponse;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.services.idp.IdpAppService;
import in.gov.abdm.abha.enrollment.services.idp.IdpService;
import in.gov.abdm.identity.domain.Identity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class IdpAppServiceTests {

    @InjectMocks
    IdpAppService idpAppService;
    @InjectMocks
    IdpService idpService;

    @Mock
    IdpAppFClient idpAppFClient;
    @Mock
    IdpAppService idpAppService2;

    private IdpSendOtpRequest idpSendOtpRequest;
    private IdpSendOtpResponse idpSendOtpResponse;
    private IdpVerifyOtpRequest idpVerifyOtpRequest;
    private IdpVerifyOtpResponse idpVerifyOtpResponse;
    private Identity identity;
    private MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto;


    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);

        idpSendOtpRequest = new IdpSendOtpRequest();
        idpSendOtpResponse = new IdpSendOtpResponse();
        idpVerifyOtpRequest = new IdpVerifyOtpRequest();
        idpVerifyOtpResponse = new IdpVerifyOtpResponse();
        identity=new Identity();
        mobileOrEmailOtpRequestDto=new MobileOrEmailOtpRequestDto();
        Kyc kyc=new Kyc();
        Kyc kyc2=new Kyc("","","","","","","");
        String s = kyc2.toString();
        s=kyc2.getName();
        s=kyc2.getAbhaNumber();
        s=kyc2.getAbhaAddress();
        s=kyc2.getYearOfBirth();
        s=kyc2.getGender();
        s=kyc2.getMobile();
        s=kyc2.getEmail();
        Response r = new Response();
        Response r2=new Response("");
        s=r2.toString();
        s=idpVerifyOtpResponse.toString();
        r.setRequestId(r2.getRequestId());
        ErrorResponse e = new ErrorResponse();
        s=e.getCode();
        s=e.getMessage();
        Map<String, String> param=new HashMap<>();
        IdpSendOtpRequest i = new IdpSendOtpRequest("",param);
        IdpSendOtpResponse i2 = new IdpSendOtpResponse(idpSendOtpResponse.getTransactionId(), idpSendOtpResponse.getOtpSentTo(),idpSendOtpResponse.getResponse(),idpSendOtpResponse.getAbhaAddress(),idpSendOtpResponse.getAuthenticated(),idpSendOtpResponse.getError());
        s=i.getScope();
        param=i.getParameters();

    }

    @AfterEach
    void tearDown()
    {
        idpSendOtpRequest = null;
        idpSendOtpResponse = null;
        idpVerifyOtpRequest=null;
        idpVerifyOtpResponse=null;
        identity=null;
        mobileOrEmailOtpRequestDto=null;

    }
    @Test
    void sendOtpSuccess()
    {
        idpSendOtpRequest=new IdpSendOtpRequest();
        idpSendOtpRequest.setScope("scope");
        idpSendOtpResponse.setTransactionId("txnId");
        Mockito.when(idpAppFClient.sendOtp(any(),any(),any(),any(),any())).thenReturn(Mono.just(idpSendOtpResponse));
        StepVerifier.create(idpAppService.sendOtp(idpSendOtpRequest,"Test","Test","Test","Test"))
               .expectNextCount(1L)
               .verifyComplete();
    }
    @Test
    void verifyOtpSuccess()
    {
      //  idpVerifyOtpRequest=new IdpSendOtpRequest();
        idpVerifyOtpRequest.setOtp("123421");
        idpVerifyOtpRequest.setTxnId("txnId");
        IdpVerifyOtpRequest i = new IdpVerifyOtpRequest(idpVerifyOtpRequest.getTxnId(), idpVerifyOtpRequest.getOtp());
        idpVerifyOtpResponse.setPreferredAbhaAddress("preferrredAddress");
        IdpVerifyOtpResponse idpVerifyOtpResponse2=new IdpVerifyOtpResponse(idpVerifyOtpResponse.getPreferredAbhaAddress(),idpVerifyOtpResponse.getKyc(),idpVerifyOtpResponse.getResponse(),idpVerifyOtpResponse.getError());
        Mockito.when(idpAppFClient.verifyOtp(any(),any(),any(),any(),any())).thenReturn(Mono.just(idpVerifyOtpResponse));
        StepVerifier.create(idpAppService.verifyOtp(idpVerifyOtpRequest,"Test","Test","Test","Test"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyAbhaAddressExistsSuccess()
    {
        String aabhaAddress="aabhaAddress";
        Mockito.when(idpAppFClient.verifyAbhaAddressExists(any(),any(),any())).thenReturn(Mono.just(true));
        StepVerifier.create(idpAppService.verifyAbhaAddressExists("aabhaAddress")).expectNext(true)
                .verifyComplete();
    }
    @Test
    void getUsersByAbhaAddressListSuccess()
    {
        List<String> abhaAddrList = new ArrayList<>();
        abhaAddrList.add("addr1");
        abhaAddrList.add("addr2");
        identity.setAbhaAddresses(abhaAddrList);
        Mockito.when(idpAppFClient.getUsersByAbhaAddressList(any(),any(),any())).thenReturn(Flux.just(identity));
        StepVerifier.create(idpAppService.getUsersByAbhaAddressList(abhaAddrList)).expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void sendOtpSuccessIdpService()
    {
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.ABHA_NUMBER);
        mobileOrEmailOtpRequestDto.setLoginId("loginId");
        idpSendOtpRequest.setScope("scope");
        idpSendOtpResponse.setTransactionId("txnId");
        Mockito.when(idpAppFClient.sendOtp(any(),any(),any(),any(),any())).thenReturn(Mono.just(idpSendOtpResponse));
        Mockito.when(idpAppService2.sendOtp(any(),any(),any(),any(),any())).thenReturn(Mono.just(idpSendOtpResponse));
        StepVerifier.create(idpService.sendOtp(mobileOrEmailOtpRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.MOBILE);
        StepVerifier.create(idpService.sendOtp(mobileOrEmailOtpRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }




}
