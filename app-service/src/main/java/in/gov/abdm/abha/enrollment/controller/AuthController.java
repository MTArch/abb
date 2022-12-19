package in.gov.abdm.abha.enrollment.controller;

import java.util.List;

import javax.validation.Valid;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.database.constraint.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.services.auth.aadhaar.AuthByAadhaarService;
import in.gov.abdm.abha.enrollment.services.auth.abdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.AUTH_ENDPOINT)
public class AuthController {
	
    @Autowired
    AuthByAbdmService authByAbdmService;
    @Autowired
    AuthByAadhaarService authByAadhaarService;
    @Autowired
    RSAUtil rsaUtil;

    @PostMapping(URIConstant.AUTH_BY_ABDM_ENDPOINT)
	public Mono<AuthResponseDto> authByABDM(@Valid @RequestBody AuthRequestDto authByAbdmRequest) {
		authByAbdmRequest.getAuthData().getOtp()
				.setOtpValue(rsaUtil.decrypt(authByAbdmRequest.getAuthData().getOtp().getOtpValue()));
		if (Common.isExactScopesMatching(authByAbdmRequest.getScope(),
				List.of(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY))) {
			return authByAbdmService.verifyOtpViaNotification(authByAbdmRequest,Boolean.TRUE);
		} else if (Common.isScopeAvailable(authByAbdmRequest.getScope(), Scopes.CHILD_ABHA_ENROL)) {
			return authByAbdmService.verifyOtp(authByAbdmRequest);
		} else if(Common.isExactScopesMatching(authByAbdmRequest.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.EMAIL_UPDATE))){
            return authByAbdmService.verifyOtpViaNotification(authByAbdmRequest,Boolean.FALSE);
        }else {
			throw new InvalidRequestException(AbhaConstants.INVALID_REQUEST);
		}
	}

    @PostMapping(URIConstant.AUTH_BY_AADHAAR_ENDPOINT)
    public Mono<AuthResponseDto> authByAadhaar(@Valid @RequestBody AuthRequestDto authByAadhaarRequestDto){
        return authByAadhaarService.verifyOtpChildAbha(authByAadhaarRequestDto);
    }
}
