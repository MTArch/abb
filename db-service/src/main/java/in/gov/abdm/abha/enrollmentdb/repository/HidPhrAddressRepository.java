package in.gov.abdm.abha.enrollmentdb.repository;
import java.util.List;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddressDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface HidPhrAddressRepository extends R2dbcRepository<HidPhrAddress, Long> {

	Flux<HidPhrAddress> findByHealthIdNumberInAndPreferredIn(List<String> healthIdNumbers, List<Integer> preferred);
	Flux<HidPhrAddress> findByPhrAddressIn(List<String> phrAddress);
	@Query("SELECT * from hid_phr_address where lower(phr_address) = :phrAddress")
	Mono<HidPhrAddress> getPhrAddressByPhrAddress(String phrAddress);
	@Query("SELECT * from hid_phr_address phr where phr.preferred = 1 AND phr.health_id_number = :healthIdNumber")
	Mono<HidPhrAddressDto> findByHealthIdNumber(String healthIdNumber);

	@Query("SELECT * from hid_phr_address phr where phr.health_id_number IN (:healthIdNumber)")
	Flux<HidPhrAddress> fetchPhrAddresses(List<String> healthIdnumbers);
}