package in.gov.abdm.abha.enrollment.services.aadhaar.impl;

import org.springframework.stereotype.Service;

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
