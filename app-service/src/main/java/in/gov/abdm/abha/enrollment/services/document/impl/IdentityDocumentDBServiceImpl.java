package in.gov.abdm.abha.enrollment.services.document.impl;

import in.gov.abdm.abha.enrollment.client.DocumentDBIdentityDocumentFClient;
import in.gov.abdm.abha.enrollment.exception.document.DocumentDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import in.gov.abdm.abha.enrollment.services.document.IdentityDocumentDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IdentityDocumentDBServiceImpl implements IdentityDocumentDBService {
     @Autowired
     DocumentDBIdentityDocumentFClient documentDBIdentityDocumentFClient;

     @Override
     public Mono<IdentityDocumentsDto> addIdentityDocuments(IdentityDocumentsDto identityDocumentsDto) {
          return documentDBIdentityDocumentFClient.addIdentityDocuments(identityDocumentsDto).doOnError((throwable->Mono.error(new DocumentDBGatewayUnavailableException())));
     }
}
