package in.gov.abdm.abha.enrollment.services.enrol.aadhaar;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.notification.SendNotificationRequestDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EnrolUsingAadhaarService {

    Mono<EnrolByAadhaarResponseDto> verifyOtp(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders);

    Mono<String> requestNotification(SendNotificationRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders);


    Mono<EnrolByAadhaarResponseDto> faceAuth(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders);

    void validateHeaders(RequestHeaders requestHeaders, List<AuthMethods> authMethods,String fToken);

    void validateNotificationRequest(SendNotificationRequestDto sendNotificationRequestDto);

}
