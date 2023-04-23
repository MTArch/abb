package in.gov.abdm.abha.enrollment.services.enrol.aadhaar;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EnrolUsingAadhaarService {

    Mono<EnrolByAadhaarResponseDto> verifyOtp(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, String benefitName, List<String> roleList, String clientId);

    Mono<EnrolByAadhaarResponseDto> faceAuth(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto);
}
