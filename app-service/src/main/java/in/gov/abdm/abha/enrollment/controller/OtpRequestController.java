package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import in.gov.abdm.abha.enrollment.enums.request.ScopeEnum;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.idp.IdpService;
import in.gov.abdm.abha.enrollment.services.otp_request.OtpRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping(ABHAEnrollmentConstant.OTP_REQUEST_ENDPOINT)
public class OtpRequestController {

    @Autowired
    OtpRequestService otpRequestService;

    @Autowired
    IdpService idpService;


    /**
     * endpoint to generate mobile or email otp for abha creation using aadhaar
     *
     * @param mobileOrEmailOtpRequestDto
     * @return txnId and success or failed message as part of responseDto
     */
    @PostMapping(ABHAEnrollmentConstant.MOBILE_OR_EMAIL_TOP_ENDPOINT)
    public Mono<MobileOrEmailOtpResponseDto> mobileOrEmailOtp( @RequestBody MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
            if (mobileOrEmailOtpRequestDto.getScope().equals(ScopeEnum.ABHA_ENROL.getValue())) {
                return otpRequestService.sendOtp(mobileOrEmailOtpRequestDto);
            }else if (mobileOrEmailOtpRequestDto.getScope().equals(ScopeEnum.MOBILE_VERIFY.getValue())) {
                return idpService.sendMobileOtpByIDP(mobileOrEmailOtpRequestDto);
            }

            else {
                return null; //TODO Handle her scope exceptions
            }
            // return otpRequestService.sendOtp(mobileOrEmailOtpRequestDto);
        }

}
