package in.gov.abdm.abha.enrollment.services.auth_byabdm;

import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
public interface AuthByAbdmService<T> {
    public Mono<IdpVerifyOtpResponse> verifyOtp();
}
