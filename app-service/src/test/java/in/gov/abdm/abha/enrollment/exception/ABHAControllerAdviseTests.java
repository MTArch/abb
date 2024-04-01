package in.gov.abdm.abha.enrollment.exception;

import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.abha_db.*;
import in.gov.abdm.abha.enrollment.exception.application.*;
import in.gov.abdm.abha.enrollment.exception.application.handler.ABHAControllerAdvise;
import in.gov.abdm.abha.enrollment.exception.document.DocumentDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.document.DocumentGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.hidbenefit.BenefitNotFoundException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationDBGatewayUnavailableException;
import in.gov.abdm.error.ABDMError;
import in.gov.abdm.error.ErrorResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
public class ABHAControllerAdviseTests {
    @InjectMocks
    ABHAControllerAdvise abhaControllerAdvise;

    @Test
    public void exception(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new Exception("msg"));
        Assert.assertNotNull(res);
    }
    @Test
    public void abhaDBGatewayUnavailableExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new AbhaDBGatewayUnavailableException("msg"));
        Assert.assertNotNull(res);
    }
    @Test
    public void notificationDBGatewayUnavailableExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new NotificationDBGatewayUnavailableException());
        Assert.assertNotNull(res);
    }
    @Test
    public void documentDBGatewayUnavailableExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new DocumentDBGatewayUnavailableException());
        Assert.assertNotNull(res);
    }
    @Test
    public void abhaUnProcessableExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new AbhaUnProcessableException(ABDMError.UN_PROCESSABLE_ENTITY.getCode(),"msg"));
        Assert.assertNotNull(res);
    }
    @Test
    public void abhaBadRequestExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new AbhaBadRequestException(ABDMError.BAD_REQUEST.getCode(),"msg"));
        Assert.assertNotNull(res);
    }
    @Test
    public void abhaBadRequestExceptionTest2(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new AbhaBadRequestException(ABDMError.BAD_REQUEST));
        Assert.assertNotNull(res);
    }
    @Test
    public void abhaUnAuthorizedExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new AbhaUnAuthorizedException(ABDMError.UNAUTHORIZED_ACCESS));
        Assert.assertNotNull(res);
    }
    @Test
    public void abhaOkExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new AbhaOkException(ABDMError.AADHAAR_DATA_NOT_MATCHED_WITH_DR.getCode(),"msg"));
        Assert.assertNotNull(res);
    }
    @Test
    public void abhaConflictExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new AbhaConflictException(ABDMError.BAD_REQUEST.getCode(),"msg"));
        Assert.assertNotNull(res);
    }
    @Test
    public void abhaDBException(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new AbhaDBException("\\[P0001] error message"));
        Assert.assertNotNull(res);
    }
    @Test
    public void nullPointerExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res =abhaControllerAdvise.exception(new Exception("\\[P0001]error message BAD_REQUEST"));
        Assert.assertNotNull(res);
    }
    @Test
    public void handleAadhaarOtpExceptionTest(){
       StepVerifier.create(abhaControllerAdvise.handleAadhaarOtpException(new AadhaarExceptions(ABDMError.AADHAAR_EXCEPTIONS.getCode())))
               .expectNextCount(1L)
               .verifyComplete();
    }
    @Test
    public void handleAadhaarOtpExceptionTest2(){
        StepVerifier.create(abhaControllerAdvise.handleAadhaarOtpException(new AadhaarExceptions(ABDMError.AADHAAR_EXCEPTIONS.getCode())))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void handleEnrolmentIdNotFoundExceptionTest(){
        ResponseEntity<Mono<ErrorResponse>> res = abhaControllerAdvise.handleEnrolmentIdNotFoundException();
        Assert.assertNotNull(res);
    }
    @Test
    public void runtimeBadRequestHandlerTest(){
        Map<String, String> message = new HashMap<>();
        message.put("1","er");
        Map<String, String> res = abhaControllerAdvise.runtimeBadRequestHandler(new BadRequestException(message));
        Assert.assertNotNull(res);
    }
    @Test
    public void invalidRequestTest(){
        Map<String, Object> res = abhaControllerAdvise.invalidRequest(new ServerWebInputException("reason",null,new Throwable("msg")));
        Assert.assertNotNull(res);
    }
    @Test
    public void invalidRequestTestScope(){
        Map<String, Object> res = abhaControllerAdvise.invalidRequest(new ServerWebInputException("reason",null,new Throwable("msg Scopes")));
        Assert.assertNotNull(res);
    }
    @Test
    public void invalidRequestTestAuthMethod(){
        Map<String, Object> res = abhaControllerAdvise.invalidRequest(new ServerWebInputException("reason",null,new Throwable("msg AuthMethod")));
        Assert.assertNotNull(res);
    }
    @Test
    public void invalidRequestTestReasons(){
        Map<String, Object> res = abhaControllerAdvise.invalidRequest(new ServerWebInputException("reason",null,new Throwable("msg reasons")));
        Assert.assertNotNull(res);
    }
    @Test
    public void invalidRequestTestftoken(){
        Map<String, Object> res = abhaControllerAdvise.invalidRequest(new ServerWebInputException("reason",null,new Throwable("msg F-token")));
        Assert.assertNotNull(res);
    }
    @Test
    public void benefitNotFoundExceptionTest(){
        Map<String, Object> res = abhaControllerAdvise.benefitNotFoundException(new BenefitNotFoundException("msg F-token"));
        Assert.assertNotNull(res);
    }
    @Test
    public void benefitNotFoundExceptionTest2(){
        Map<String, Object> res = abhaControllerAdvise.benefitNotFoundException(new BenefitNotFoundException());
        Assert.assertNotNull(res);
    }
    Exception e= new AbhaDBException();
    Exception e1= new EnrolmentIdNotFoundException();
    Exception e2= new InvalidRequestException();
    Exception e3 = new TransactionNotFoundException();



}
