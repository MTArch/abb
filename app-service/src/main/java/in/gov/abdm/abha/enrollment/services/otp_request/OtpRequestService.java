package in.gov.abdm.abha.enrollment.services.otp_request;

import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import reactor.core.publisher.Mono;

/**
 * service for OTP Request coming from ui
 * otp can be sent via aadhaar / abdm
 */
public interface OtpRequestService {
    /**
     * To send OTP via aadhaar / abdm
     * @param mobileOrEmailOtpRequestDto
     * @return
     */
    Mono<MobileOrEmailOtpResponseDto> sendOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto);
}
