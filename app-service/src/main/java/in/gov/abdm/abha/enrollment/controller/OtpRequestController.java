package in.gov.abdm.abha.enrollment.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.database.constraint.InvalidRequestException;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.otp_request.OtpRequestService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.OTP_REQUEST_ENDPOINT)
public class OtpRequestController {

    @Autowired
    OtpRequestService otpRequestService;

    /**
     * endpoint to generate mobile or email otp for abha creation using aadhaar
     *
     * @param mobileOrEmailOtpRequestDto
     * @return txnId and success or failed message as part of responseDto
     */
    @PostMapping(URIConstant.MOBILE_OR_EMAIL_OTP_ENDPOINT)
    public Mono<MobileOrEmailOtpResponseDto> mobileOrEmailOtp(@Valid @RequestBody MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {

        //filter scope
        List<Scopes> requestScopes = mobileOrEmailOtpRequestDto.getScope();
        // If scope -abha-enrol and mobile-verify and otpSystem -abdm
        if (Common.isAllScopesAvailable(requestScopes, List.of(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY))
                && mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM) ) {
            return otpRequestService.sendOtpViaNotificationService(mobileOrEmailOtpRequestDto);
        }
        else if(Common.isAllScopesAvailable(requestScopes, List.of(Scopes.ABHA_ENROL, Scopes.EMAIL_VERIFY))
                && mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM)){
            return otpRequestService.sendEmailOtpViaNotificationService(mobileOrEmailOtpRequestDto);
        }
        // If scope -abha-enrol or -child-abha-enrol abd  otpSystem -aadhaar
        else if ((Common.isScopeAvailable(requestScopes, Scopes.ABHA_ENROL)
        		||Common.isScopeAvailable(requestScopes, Scopes.CHILD_ABHA_ENROL))
                && mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.AADHAAR)) {
            return otpRequestService.sendAadhaarOtp(mobileOrEmailOtpRequestDto);
        }
        // If scope -child-abha-enrol
        else if(Common.isScopeAvailable(requestScopes, Scopes.CHILD_ABHA_ENROL)
        		  && mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM)){
            return otpRequestService.sendIdpOtp(mobileOrEmailOtpRequestDto);
        }
        // other case
        else{
        	throw new InvalidRequestException(AbhaConstants.INVALID_REQUEST);
        }
    }
}
