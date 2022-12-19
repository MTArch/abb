package in.gov.abdm.abha.enrollment.services.auth.abdm;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import reactor.core.publisher.Mono;

public interface AuthByAbdmService {
    Mono<AuthResponseDto> verifyOtp(AuthRequestDto authByAbdmRequest);

    Mono<AuthResponseDto> verifyOtpViaNotification(AuthRequestDto authByAbdmRequest,boolean isMobile);
}
