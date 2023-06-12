package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import in.gov.abdm.identity.domain.Identity;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_IDP_BASEURI;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.*;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;
import static in.gov.abdm.constant.ABDMConstant.TIMESTAMP;

@ReactiveFeignClient(name= AbhaConstants.IDP_APP_CLIENT, url=ENROLLMENT_GATEWAY_IDP_BASEURI, configuration = BeanConfiguration.class)
public interface IdpAppFClient {

    @PostMapping(URIConstant.IDP_SEND_OTP_URI)
    public Mono<IdpSendOtpResponse> sendOtp(@RequestBody IdpSendOtpRequest idpSendOtpRequest,@PathVariable String authorization, @RequestHeader(TIMESTAMP) String timestamp, @RequestHeader(REQUESTER_ID) String hipRequestId, @RequestHeader(REQUEST_ID) String requestId);

    @PostMapping(URIConstant.IDP_VERIFY_OTP_URI)
    public Mono<IdpVerifyOtpResponse> verifyOtp(@RequestBody IdpVerifyOtpRequest idpVerifyOtpRequest,@PathVariable String authorization,@RequestHeader(TIMESTAMP) String timeStamp, @RequestHeader(REQUESTER_ID)String hipRequestId, @RequestHeader(REQUEST_ID) String requestId);

    @GetMapping(URIConstant.IDENTITY_VERIFY_ABHA_EXISTS)
    public Mono<Boolean> verifyAbhaAddressExists(@PathVariable("abhaAddress") String abhaAddress,@RequestHeader(REQUEST_ID) UUID requestId,@RequestHeader(TIMESTAMP) String timestamp);

    @GetMapping(URIConstant.GET_IDENTITY_BY_ABHA_ADDRESS_LIST_URI)
    public Flux<Identity> getUsersByAbhaAddressList(@PathVariable("abhaAddressList") String abhaAddress, @RequestHeader(REQUEST_ID) UUID requestId, @RequestHeader(TIMESTAMP) String timestamp);
}
