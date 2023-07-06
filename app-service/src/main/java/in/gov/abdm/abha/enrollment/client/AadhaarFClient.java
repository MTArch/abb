package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyBioRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyFaceAuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_AADHAAR_BASEURI;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.*;


@ReactiveFeignClient(name= AbhaConstants.AADHAAR_SERVICE_CLIENT, url=ENROLLMENT_GATEWAY_AADHAAR_BASEURI, configuration = BeanConfiguration.class)
public interface AadhaarFClient {

    @PostMapping(URIConstant.AADHAAR_SEND_OTP_URI)
    public Mono<AadhaarResponseDto> sendOtp(@RequestBody AadhaarOtpRequestDto aadhaarOtpRequestDto);

    @PostMapping(URIConstant.AADHAAR_VERIFY_OTP_URI)
    public Mono<AadhaarResponseDto> verifyOtp(@RequestBody AadhaarVerifyOtpRequestDto aadhaarVerifyOtpRequestDto);

    @PostMapping(URIConstant.AADHAAR_VERIFY_DEMOGRAPHIC)
    public Mono<VerifyDemographicResponse> verifyDemographicDetails(VerifyDemographicRequest verifyDemographicRequest);

    @PostMapping(AADHAAR_VERIFY_FACE)
    public Mono<AadhaarResponseDto> faceAuth(@RequestBody AadhaarVerifyFaceAuthRequestDto aadhaarVerifyFaceAuthRequestDto);

    @PostMapping(AADHAAR_VERIFY_BIO)
    public Mono<AadhaarResponseDto> verifyBio(@RequestBody AadhaarVerifyBioRequestDto aadhaarVerifyBioRequestDto);

    @PostMapping(AADHAAR_VERIFY_IRIS)
    public Mono<AadhaarResponseDto> verifyIris(@RequestBody AadhaarVerifyBioRequestDto aadhaarVerifyBioRequestDto);

}
