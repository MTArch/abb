package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.services.auth_byabdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.AUTH_ENDPOINT)
public class AuthController {
	
    @Autowired
    AuthByAbdmService authByAbdmService;
    @Autowired
    EnrolUsingAadhaarService enrolUsingAadhaarService;
    @Autowired
    RSAUtil rsaUtil;

    @PostMapping(URIConstant.AUTH_BY_ABDM_ENDPOINT)
    public Mono<AuthResponseDto> authByABDM(@Valid @RequestBody AuthByAbdmRequest authByAbdmRequest){
        authByAbdmRequest.getAuthData().getOtp().setOtpValue(rsaUtil.decrypt(authByAbdmRequest.getAuthData().getOtp().getOtpValue()));
        if(Common.isExactScopesMatching(authByAbdmRequest.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY))){
            return authByAbdmService.verifyOtpViaNotification(authByAbdmRequest);
        }
      return authByAbdmService.verifyOtp(authByAbdmRequest);
    }

    @PostMapping(URIConstant.AUTH_BY_AADHAAR_ENDPOINT)
    public Mono<AuthResponseDto> authByAadhaar( @RequestBody AuthByAadhaarRequestDto authByAadhaarRequestDto){
        return enrolUsingAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto);
    }
}
