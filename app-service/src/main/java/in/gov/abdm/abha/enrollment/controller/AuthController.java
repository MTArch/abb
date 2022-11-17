package in.gov.abdm.abha.enrollment.controller;

import javax.validation.Valid;

import in.gov.abdm.abha.enrollment.constants.EnrollConstant;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmRequest;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmResponse;
import in.gov.abdm.abha.enrollment.services.auth_byabdm.AuthByAbdmService;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping(EnrollConstant.AUTH_ENDPOINT)
public class AuthController {
	
    @Autowired
    AuthByAbdmService authByAbdmService;
    @Autowired
    EnrolUsingAadhaarService enrolUsingAadhaarService;

    @PostMapping(EnrollConstant.AUTH_BY_ABDM_ENDPOINT)
    public Mono<AuthByAbdmResponse> authByABDM(@Valid @RequestBody AuthByAbdmRequest authByAbdmRequest){
      return authByAbdmService.verifyOtp();
    }

    @PostMapping(EnrollConstant.AUTH_BY_AADHAAR_ENDPOINT)
    public Mono<AuthByAadhaarResponseDto> authByAadhaar(@RequestBody AuthByAadhaarRequestDto authByAadhaarRequestDto){
        return enrolUsingAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto);
    }
}
