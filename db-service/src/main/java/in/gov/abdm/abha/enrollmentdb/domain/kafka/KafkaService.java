package in.gov.abdm.abha.enrollmentdb.domain.kafka;

import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import reactor.core.publisher.Mono;

public interface KafkaService {
    Mono<Void> publishPhrUserPatientEvent(HidPhrAddress hidPhrAddress);

    Mono<Void> publishPhrUserPatientEventByAccounts(AccountDto accountDto);
}
