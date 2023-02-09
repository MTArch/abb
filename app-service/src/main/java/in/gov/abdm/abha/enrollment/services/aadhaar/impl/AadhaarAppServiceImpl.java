package in.gov.abdm.abha.enrollment.services.aadhaar.impl;

import in.gov.abdm.abha.enrollment.client.AadhaarFClient;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AadhaarAppServiceImpl implements AadhaarAppService {
    @Autowired
    AadhaarFClient aadhaarFClient;

    public Mono<AadhaarResponseDto> sendOtp(AadhaarOtpRequestDto aadhaarOtpRequestDto){
        return aadhaarFClient.sendOtp(aadhaarOtpRequestDto).doOnError(throwable -> Mono.error(new AadhaarGatewayUnavailableException()));
    }

    public Mono<AadhaarResponseDto> verifyOtp(AadhaarVerifyOtpRequestDto aadhaarVerifyOtpRequestDto){
        return aadhaarFClient.verifyOtp(aadhaarVerifyOtpRequestDto).doOnError(throwable -> Mono.error(new AadhaarGatewayUnavailableException()));
    }

    public Mono<VerifyDemographicResponse> verifyDemographicDetails(VerifyDemographicRequest verifyDemographicRequest){
        return aadhaarFClient.verifyDemographicDetails(verifyDemographicRequest).doOnError(throwable -> Mono.error(new AadhaarGatewayUnavailableException()));
    }
}
