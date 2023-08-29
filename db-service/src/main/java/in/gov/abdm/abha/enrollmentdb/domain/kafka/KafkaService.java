package in.gov.abdm.abha.enrollmentdb.domain.kafka;

import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import reactor.core.publisher.Mono;

public interface KafkaService {
	Mono<Void> publishPhrUserPatientEvent(HidPhrAddress hidPhrAddress);

	Mono<Void> publishPhrUserPatientEventByAccounts(AccountDto accountDto);

	Mono<Void> publishDashBoardAbhaEventByAccounts(AccountReattemptDto RaccountDto);
}
