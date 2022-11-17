package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.EnrollConstant;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.otp_request.OtpRequestService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(EnrollConstant.OTP_REQUEST_ENDPOINT)
public class OtpRequestController {

    private static final String FAILED_TO_SEND_OTP = "Failed to send OTP";

    @Autowired
    OtpRequestService otpRequestService;

    /**
     * endpoint to generate mobile or email otp for abha creation using aadhaar
     *
     * @param mobileOrEmailOtpRequestDto
     * @return txnId and success or failed message as part of responseDto
     */
    @PostMapping(EnrollConstant.MOBILE_OR_EMAIL_TOP_ENDPOINT)
    public Mono<MobileOrEmailOtpResponseDto> mobileOrEmailOtp(@RequestBody MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {

        //filter scope
        List<Scopes> requestScopes = mobileOrEmailOtpRequestDto.getScope();
        String otpSystem = mobileOrEmailOtpRequestDto.getOtpSystem();
        // If scope -abha-enrol and mobile-verify and otpSystem -abdm
        if (Common.isAllScopesAvailable(requestScopes, List.of(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY))
                && Common.isOtpSystem(otpSystem, OtpSystem.ABDM)) {
            return otpRequestService.sendOtpViaNotificationService(mobileOrEmailOtpRequestDto);
        }
        // If scope -abha-enrol and otpSystem -aadhaar
        else if (Common.isScopeAvailable(requestScopes, Scopes.ABHA_ENROL)
                && Common.isOtpSystem(otpSystem, OtpSystem.AADHAAR)) {
            return otpRequestService.sendAadhaarOtp(mobileOrEmailOtpRequestDto);
        }
        // If scope -child-abha-enrol
        else if(Common.isScopeAvailable(requestScopes, Scopes.CHILD_ABHA_ENROL)){
            return otpRequestService.sendIdpOtp(mobileOrEmailOtpRequestDto);
        }
        // other case
        else{
            throw new GenericExceptionMessage(FAILED_TO_SEND_OTP);
        }
    }
}
