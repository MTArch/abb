package in.gov.abdm.abha.enrollment.services.document.impl;

import in.gov.abdm.abha.enrollment.client.DocumentAppFClient;
import in.gov.abdm.abha.enrollment.client.DocumentClient;
import in.gov.abdm.abha.enrollment.exception.document.DocumentGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLRequest;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import in.gov.abdm.abha.enrollment.services.document.DocumentAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DocumentAppServiceImpl implements DocumentAppService {

    @Autowired
    DocumentAppFClient documentAppFClient;

    @Override
    public Mono<VerifyDLResponse> verify(VerifyDLRequest verifyDLRequest) {
        return documentAppFClient.verify(verifyDLRequest).onErrorResume(throwable -> Mono.error(new DocumentGatewayUnavailableException()));
    }
}
