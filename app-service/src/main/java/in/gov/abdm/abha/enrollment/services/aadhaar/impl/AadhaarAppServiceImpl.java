package in.gov.abdm.abha.enrollment.services.aadhaar.impl;

import in.gov.abdm.abha.enrollment.client.AadhaarFClient;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyBioRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyFaceAuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AadhaarAppServiceImpl implements AadhaarAppService {
    @Autowired
    AadhaarFClient aadhaarFClient;
    private static final String AADHAAR_ERROR_MESSAGE = "Aadhaar service error {}";

    public Mono<AadhaarResponseDto> sendOtp(AadhaarOtpRequestDto aadhaarOtpRequestDto){
        return aadhaarFClient.sendOtp(aadhaarOtpRequestDto).onErrorResume(throwable -> {
            log.error(AADHAAR_ERROR_MESSAGE,throwable.getMessage());
            return Mono.error(new AadhaarGatewayUnavailableException(new Exception(throwable)));
        });
    }

    public Mono<AadhaarResponseDto> verifyOtp(AadhaarVerifyOtpRequestDto aadhaarVerifyOtpRequestDto){
        return aadhaarFClient.verifyOtp(aadhaarVerifyOtpRequestDto).onErrorResume(throwable -> {
            log.error(AADHAAR_ERROR_MESSAGE,throwable.getMessage());
            return Mono.error(new AadhaarGatewayUnavailableException(new Exception(throwable)));
        });
    }

    public Mono<VerifyDemographicResponse> verifyDemographicDetails(VerifyDemographicRequest verifyDemographicRequest){
        return aadhaarFClient.verifyDemographicDetails(verifyDemographicRequest).onErrorResume(throwable -> {
            log.error(AADHAAR_ERROR_MESSAGE,throwable.getMessage());
            return Mono.error(new AadhaarGatewayUnavailableException(new Exception(throwable)));
        });
    }

    public Mono<AadhaarResponseDto> faceAuth(AadhaarVerifyFaceAuthRequestDto aadhaarVerifyFaceAuthRequestDto){
        return aadhaarFClient.faceAuth(aadhaarVerifyFaceAuthRequestDto).onErrorResume(throwable -> {
            log.error(AADHAAR_ERROR_MESSAGE,throwable.getMessage());
            return Mono.error(new AadhaarGatewayUnavailableException(new Exception(throwable)));
        });
    }
    public Mono<AadhaarResponseDto> verifyBio(AadhaarVerifyBioRequestDto aadhaarVerifyBioRequestDto){
        return aadhaarFClient.verifyBio(aadhaarVerifyBioRequestDto).onErrorResume(throwable -> {
            log.error(AADHAAR_ERROR_MESSAGE,throwable.getMessage());
            return Mono.error(new AadhaarGatewayUnavailableException(new Exception(throwable)));
        });
    }

    public Mono<AadhaarResponseDto> verifyIris(AadhaarVerifyBioRequestDto aadhaarVerifyBioRequestDto){
        return aadhaarFClient.verifyIris(aadhaarVerifyBioRequestDto).onErrorResume(throwable -> {
            log.error(AADHAAR_ERROR_MESSAGE,throwable.getMessage());
            return Mono.error(new AadhaarGatewayUnavailableException(new Exception(throwable)));
        });
    }
}
