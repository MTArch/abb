package in.gov.abdm.abha.enrollment.services.document;

import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface IdentityDocumentDBService {
     Mono<IdentityDocumentsDto> addIdentityDocuments(IdentityDocumentsDto identityDocumentsDto);
}
