package in.gov.abdm.abha.enrollmentdb.domain.idp;

import java.sql.Timestamp;
import java.util.List;

import in.gov.abdm.identity.domain.Identity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IdentityService {
	Mono<Identity> getAccountByHealthIdNumberAndAbhaAddress(String healthIdNumber, String abhaAddress);

	Mono<Identity> getAccountByHealthIdNumber(String healthIdNumber);

	Mono<Identity> findUserByAbhaAddress(String requestId, Timestamp timestamp, String abhaAddress);

	Flux<Identity> findUsersByMobileNumber(String requestId, Timestamp timestamp, String mobileNumber);

	Flux<Identity> findAllAbhaAddressByMobileNumber(String requestId, Timestamp timestamp, String mobileNumber);

	Mono<Identity> findUserWithoutStatusByAbhaAddress(String requestId, Timestamp timestamp, String abhaAddress);

	Flux<Identity> findUserByAbhaAddressList(String requestId, Timestamp timestamp, List<String> abhaAddressList);

}
