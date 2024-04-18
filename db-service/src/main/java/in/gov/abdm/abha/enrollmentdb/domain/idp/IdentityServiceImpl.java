package in.gov.abdm.abha.enrollmentdb.domain.idp;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.repository.IdentityRepository;
import in.gov.abdm.identity.domain.Identity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IdentityServiceImpl implements IdentityService {

	@Autowired
	IdentityRepository identityRepository;

	@Override
	public Mono<Identity> getAccountByHealthIdNumberAndAbhaAddress(String healthIdNumber, String abhaAddress) {
		return identityRepository.getAccountsByHealthIdNumberAndAbhaAddress(healthIdNumber, abhaAddress);
	}

	@Override
	public Mono<Identity> getAccountByHealthIdNumber(String healthIdNumber) {
		return identityRepository.getAccountsByHealthIdNumber(healthIdNumber);
	}

	@Override
	public Mono<Identity> findUserByAbhaAddress(String requestId, Timestamp timestamp, String abhaAddress) {

		return identityRepository.findUserByAbhaAddress(abhaAddress);
	}

	@Override
	public Flux<Identity> findUsersByMobileNumber(String requestId, Timestamp timestamp, String mobileNumber) {
		return identityRepository.findUsersByMobileNumber(mobileNumber);
	}

	@Override
	public Flux<Identity> findAllAbhaAddressByMobileNumber(String requestId, Timestamp timestamp, String mobileNumber) {
		return identityRepository.findAllAbhaAddressByMobileNumber(mobileNumber);
	}

	@Override
	public Mono<Identity> findUserWithoutStatusByAbhaAddress(String requestId, Timestamp timestamp,
			String abhaAddress) {
		return identityRepository.findUserWithoutStatusByAbhaAddress(abhaAddress);
	}

	@Override
	public Flux<Identity> findUserByAbhaAddressList(String requestId, Timestamp timestamp,
			List<String> abhaAddressList) {
		return identityRepository.findUserByAbhaAddressList(abhaAddressList);
	}

}
