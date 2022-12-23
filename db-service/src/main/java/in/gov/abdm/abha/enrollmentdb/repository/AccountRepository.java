package in.gov.abdm.abha.enrollmentdb.repository;

import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface AccountRepository extends ReactiveCrudRepository<Accounts, String> {
    Mono<Accounts> findByXmluid(@Param("xmluid") String xmluid);

    Mono<Accounts> findByDocumentCode(@Param("documentCode") String documentCode);
}