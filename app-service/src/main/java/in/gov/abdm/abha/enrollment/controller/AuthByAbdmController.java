package in.gov.abdm.abha.enrollment.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmRequest;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmResponse;
import in.gov.abdm.abha.enrollment.services.auth_byabdm.AuthByAbdmService;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping(ABHAEnrollmentConstant.AUTH_BY_ABDM_BASE_URI)
public class AuthByAbdmController {
	
    @Autowired
    AuthByAbdmService authByAbdmService;

    @PostMapping(ABHAEnrollmentConstant.AUTH_BY_ABDM)
    public Mono<AuthByAbdmResponse> authByAbdm(@Valid @RequestBody AuthByAbdmRequest authByAbdmRequest){
      return authByAbdmService.verifyOtp();
    }
}
