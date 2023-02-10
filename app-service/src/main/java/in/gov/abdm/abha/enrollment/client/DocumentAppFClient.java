package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLRequest;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name= AbhaConstants.DOCUMENT_APP_CLIENT, url="${enrollment.gateway.document.baseuri}", configuration = BeanConfiguration.class)
public interface DocumentAppFClient {

    @PostMapping(URIConstant.DOCUMENT_VERIFY)
    public Mono<VerifyDLResponse> verify(VerifyDLRequest verifyDLRequest);

}
