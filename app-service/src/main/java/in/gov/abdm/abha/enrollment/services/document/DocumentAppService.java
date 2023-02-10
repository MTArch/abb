package in.gov.abdm.abha.enrollment.services.document;

import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLRequest;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface DocumentAppService {
     Mono<VerifyDLResponse> verify(VerifyDLRequest verifyDLRequest);
}
