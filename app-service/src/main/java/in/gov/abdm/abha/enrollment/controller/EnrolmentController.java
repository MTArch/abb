package in.gov.abdm.abha.enrollment.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping(ABHAEnrollmentConstant.ENROL_ENDPOINT)
public class EnrolmentController {

    @Autowired
    RSAUtil rsaUtil;

    @Autowired
    EnrolUsingAadhaarService enrolUsingAadhaarService;

    @PostMapping(ABHAEnrollmentConstant.BY_ENROL_AADHAAR_ENDPOINT)
    public Mono<EnrolByAadhaarResponseDto> enrolUsingAadhaar(@Valid @RequestBody EnrolByAadhaarRequestDto enrolByAadhaarRequestDto){
        return enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto);
    }

    @PostMapping(ABHAEnrollmentConstant.BY_AUTH_AADHAAR_ENDPOINT)
    public Mono<AuthByAadhaarResponseDto> enrolchildAbhaUsingAadhaar(@RequestBody AuthByAadhaarRequestDto authByAadhaarRequestDto){
        return enrolUsingAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto);
    }
}
