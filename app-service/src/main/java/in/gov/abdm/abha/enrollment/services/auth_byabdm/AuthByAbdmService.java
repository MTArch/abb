package in.gov.abdm.abha.enrollment.services.auth_byabdm;

import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import reactor.core.publisher.Mono;

public interface AuthByAbdmService {
    Mono<AuthResponseDto> verifyOtp(AuthByAbdmRequest authByAbdmRequest);

    Mono<AuthResponseDto> verifyOtpViaNotification(AuthByAbdmRequest authByAbdmRequest);
}
