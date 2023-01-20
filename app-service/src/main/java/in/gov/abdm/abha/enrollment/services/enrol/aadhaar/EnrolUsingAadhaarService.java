package in.gov.abdm.abha.enrollment.services.enrol.aadhaar;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import reactor.core.publisher.Mono;

public interface EnrolUsingAadhaarService {

    Mono<EnrolByAadhaarResponseDto> verifyOtp(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto);

}
