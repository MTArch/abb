package in.gov.abdm.abha.enrollment.services.aadhaar;

import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import reactor.core.publisher.Mono;

public interface AadhaarAppService {

    Mono<AadhaarResponseDto> sendOtp(AadhaarOtpRequestDto aadhaarOtpRequestDto);

    Mono<AadhaarResponseDto> verifyOtp(AadhaarVerifyOtpRequestDto aadhaarVerifyOtpRequestDto);

    Mono<VerifyDemographicResponse> verifyDemographicDetails(VerifyDemographicRequest verifyDemographicRequest);

    Mono<AadhaarResponseDto> faceAuth(AadhaarVerifyOtpRequestDto aadhaarVerifyOtpRequestDto);
}
