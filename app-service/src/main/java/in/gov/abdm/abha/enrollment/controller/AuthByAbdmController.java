package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmRequest;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmResponse;
import in.gov.abdm.abha.enrollment.services.auth_byabdm.AuthByAbdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(ABHAEnrollmentConstant.AUTH_BYABDM_BASE_URI)

public class AuthByAbdmController {
    @Autowired
    AuthByAbdmService authByAbdmService;

    @PostMapping(ABHAEnrollmentConstant.AUTH_BYABDM)
    public Mono<AuthByAbdmResponse> authByAbdm(@Valid @RequestBody AuthByAbdmRequest authByAbdmRequest){
      return authByAbdmService.verifyOtp();
    }
}
