package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.application.AbhaBadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.services.auth.aadhaar.AuthByAadhaarService;
import in.gov.abdm.abha.enrollment.services.auth.abdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.error.ABDMError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.AUTH_ENDPOINT)
@ResponseStatus(HttpStatus.OK)
public class AuthController {

    @Autowired
    AuthByAbdmService authByAbdmService;
    @Autowired
    AuthByAadhaarService authByAadhaarService;
    @Autowired
    RSAUtil rsaUtil;

    @PostMapping(URIConstant.AUTH_BY_ABDM_ENDPOINT)
    public Mono<AuthResponseDto> authByABDM(@Valid @RequestBody AuthRequestDto authByAbdmRequest) {
        authByAbdmRequest.getAuthData().getOtp().setOtpValue(rsaUtil.decrypt(authByAbdmRequest.getAuthData().getOtp().getOtpValue()));
        //Enrol By DL otp verify flow
        if (Common.isAllScopesAvailable(authByAbdmRequest.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.DL_FLOW, Scopes.MOBILE_VERIFY))) {
            return authByAbdmService.verifyOtpViaNotificationDLFlow(authByAbdmRequest);
        }
        //Enrol mobile update otp verify flow
        else if (Common.isExactScopesMatching(authByAbdmRequest.getScope(),
                List.of(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY))) {
            return authByAbdmService.verifyOtpViaNotification(authByAbdmRequest, Boolean.TRUE);
        }
        //child abha parent link otp verify flow
        else if (Common.isScopeAvailable(authByAbdmRequest.getScope(), Scopes.CHILD_ABHA_ENROL)) {
            return authByAbdmService.verifyOtp(authByAbdmRequest);
        }
        //enrol email verify flow
        else if (Common.isExactScopesMatching(authByAbdmRequest.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.EMAIL_VERIFY))) {
            return authByAbdmService.verifyOtpViaNotification(authByAbdmRequest, Boolean.FALSE);
        } else {
            throw new AbhaBadRequestException(ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getCode(),ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getMessage());
        }
    }

    @PostMapping(URIConstant.AUTH_BY_AADHAAR_ENDPOINT)
    public Mono<AuthResponseDto> authByAadhaar(@Valid @RequestBody AuthRequestDto authByAadhaarRequestDto) {
        return authByAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto);
    }
}
