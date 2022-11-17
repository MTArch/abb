package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import in.gov.abdm.abha.enrollment.constants.EnrollConstant;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import reactor.core.publisher.Mono;

@Component
public class AadhaarClient {

    @Autowired
    private WebClient.Builder webClient;

    @Value("${abdm.aadhaar.service}")
    private String aadhaarService;

    public Mono<AadhaarResponseDto> sendOtp(AadhaarOtpRequestDto aadhaarOtpRequestDto) {
        return webClient.baseUrl(EnrollConstant.GLOBAL_SERVICE_BASE_URI)
                .build()
                .post()
                .uri(EnrollConstant.AADHAAR_SEND_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(aadhaarOtpRequestDto))
                .retrieve()
                .bodyToMono(AadhaarResponseDto.class);
    }

    public Mono<AadhaarResponseDto> verifyOtp(AadhaarVerifyOtpRequestDto aadhaarVerifyOtpRequestDto) {
        return webClient.baseUrl(EnrollConstant.GLOBAL_SERVICE_BASE_URI)
                .build()
                .post()
                .uri(EnrollConstant.AADHAAR_VERIFY_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(aadhaarVerifyOtpRequestDto))
                .retrieve()
                .bodyToMono(AadhaarResponseDto.class);
    }

    public Mono<VerifyDemographicResponse> verifyDemographicDetails(VerifyDemographicRequest verifyDemographicRequest){
        return webClient.baseUrl("http://localhost:9292")
                .build()
                .post()
                .uri(EnrollConstant.AADHAAR_VERIFY_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(verifyDemographicRequest))
                .retrieve()
                .bodyToMono(VerifyDemographicResponse.class);
    }
}
