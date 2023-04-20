package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLRequest;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import org.springframework.web.bind.annotation.PostMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_DOCUMENT_BASEURI;

@ReactiveFeignClient(name= AbhaConstants.DOCUMENT_APP_CLIENT, url=ENROLLMENT_GATEWAY_DOCUMENT_BASEURI, configuration = BeanConfiguration.class)
public interface DocumentAppFClient {

    @PostMapping(URIConstant.DOCUMENT_VERIFY)
    public Mono<VerifyDLResponse> verify(VerifyDLRequest verifyDLRequest);

}
