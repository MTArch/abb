package in.gov.abdm.abha.enrollment.services.aadhaar.impl;

import in.gov.abdm.abha.enrollment.client.ABHAEnrollmentDBClient;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarService;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AadhaarServiceImpl  {

//    @Autowired
//    private WebClient webClient;
//
//    @Value("${aadhaar.uri.otp.verify}")
//    String verifyOtpUri;
//    @Value("${aadhaar.uri.otp.send}")
//    String sendOtpUri;
//
//    @Override
//    public Mono<AadhaarResponseDto> sendOtp(AadhaarOtpRequestDto aadhaarOtpRequestDto) {
//        //TODO call aadhaar global service
//        //expecting aadhaar number in encrypted format only
//
//        return webClient.post()
//                .uri(sendOtpUri)
//                .body(BodyInserters.fromValue(aadhaarOtpRequestDto))
//                .retrieve()
//                .bodyToMono(AadhaarResponseDto.class);
//    }
//
//    @Override
//    public Mono<AadhaarResponseDto> verifyOtp(AadhaarVerifyOtpRequestDto aadhaarVerifyOtpRequestDto) {
//        //TODO call aadhaar verify otp
//
//        return webClient.post()
//                .uri(verifyOtpUri)
//                .body(BodyInserters.fromValue(aadhaarVerifyOtpRequestDto))
//                .retrieve()
//                .bodyToMono(AadhaarResponseDto.class);
//    }
}
