package in.gov.abdm.abha.enrollmentdb.repository;

import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import reactor.core.publisher.Flux;

public interface HidPhrAddressRepository extends ReactiveCrudRepository<HidPhrAddress, Long> {

	Flux<HidPhrAddress> findByHealthIdNumberInAndPreferredIn(List<String> healthIdNumbers, List<Integer> preferred);

}
