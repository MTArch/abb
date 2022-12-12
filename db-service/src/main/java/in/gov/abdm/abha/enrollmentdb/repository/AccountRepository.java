package in.gov.abdm.abha.enrollmentdb.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface AccountRepository extends ReactiveCrudRepository<Accounts, String> {
     Mono<Accounts> findByXmluid(@Param("xmluid") String xmluid);
     
     Flux<Accounts> findByHealthIdNumberIn(List<String> healthIdNumbers);

}