package in.gov.abdm.abha.enrollmentdb.domain.procedure;

import in.gov.abdm.abha.enrollmentdb.model.procedure.SaveAllDataRequest;
import reactor.core.publisher.Mono;

public interface ProcedureService {
    Mono<String> saveAllData(SaveAllDataRequest saveAllDataRequest);
}
