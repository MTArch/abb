package in.gov.abdm.abha.enrollmentdb.repository;

import java.util.List;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gov.abdm.identity.domain.Identity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IdentityRepository extends R2dbcRepository<Identity, String> {

	@Query(value = "SELECT acc.health_id_number AS abha_number,acc_phr.phr_address AS abha_address,CASE WHEN (acc_phr.preferred = 1) THEN acc_phr.phr_address ELSE (acc.health_id)::character varying END AS preferred_abha_address,acc.name,acc.kycdob AS date_of_birth,acc.gender,acc.mobile,acc.email_verified AS email,acc.type AS abha_type,NULL::text AS parent_id,acc.status,acc.state_name AS state,acc.district_name AS district,acc.address,acc.created_date,acc.day_of_birth,acc.month_of_birth,acc.year_of_birth,acc.xmluid,'accounts'::text AS tb_nm    FROM (accounts acc LEFT JOIN hid_phr_address acc_phr ON (((acc.health_id_number)::text = (acc_phr.health_id_number)::text))) where acc.health_id_number= :abhaNumber and acc_phr.phr_address = :abhaAddress AND acc.status='ACTIVE' order by created_date desc limit 1")
	Mono<Identity> getAccountsByHealthIdNumberAndAbhaAddress(@Param("abhaNumber") String healthIdNumber,
			@Param("abhaAddress") String abhaAddress);

	@Query(value = "SELECT acc.health_id_number AS abha_number,acc_phr.phr_address AS abha_address,CASE WHEN (acc_phr.preferred = 1) THEN acc_phr.phr_address ELSE (acc.health_id)::character varying END AS preferred_abha_address,acc.name,acc.kycdob AS date_of_birth,acc.gender,acc.mobile,acc.email_verified AS email,acc.type AS abha_type,NULL::text AS parent_id,acc.status,acc.state_name AS state,acc.district_name AS district,acc.address,acc.created_date,acc.day_of_birth,acc.month_of_birth,acc.year_of_birth,acc.xmluid,'accounts'::text AS tb_nm    FROM (accounts acc LEFT JOIN hid_phr_address acc_phr ON (((acc.health_id_number)::text = (acc_phr.health_id_number)::text))) where acc.health_id_number= :abhaNumber AND acc.status='ACTIVE' order by created_date desc limit 1")
	Mono<Identity> getAccountsByHealthIdNumber(@Param("abhaNumber") String healthIdNumber);

	@Query(value = "SELECT acc.health_id_number AS abha_number,acc_phr.phr_address AS abha_address,CASE WHEN (acc_phr.preferred = 1) THEN acc_phr.phr_address ELSE (acc.health_id)::character varying END AS preferred_abha_address,acc.name,acc.kycdob AS date_of_birth,acc.gender,acc.mobile,acc.email_verified AS email,acc.type AS abha_type,NULL::text AS parent_id,acc.status,acc.state_name AS state,acc.district_name AS district,acc.address,acc.created_date,acc.day_of_birth,acc.month_of_birth,acc.year_of_birth,acc.xmluid,'accounts'::text AS tb_nm    FROM (accounts acc LEFT JOIN hid_phr_address acc_phr ON (((acc.health_id_number)::text = (acc_phr.health_id_number)::text))) where acc_phr.phr_address = :abhaAddress AND acc.status='ACTIVE' order by created_date desc limit 1")
	Mono<Identity> findUserByAbhaAddress(String abhaAddress);

	@Query(value = "SELECT DISTINCT acc.health_id_number AS abha_number,acc_phr.phr_address AS abha_address,CASE WHEN (acc_phr.preferred = 1) THEN acc_phr.phr_address ELSE (acc.health_id)::character varying END AS preferred_abha_address,acc.name,acc.kycdob AS date_of_birth,acc.gender,acc.mobile,acc.email_verified AS email,acc.type AS abha_type,NULL::text AS parent_id,acc.status,acc.state_name AS state,acc.district_name AS district,acc.address,acc.created_date,acc.day_of_birth,acc.month_of_birth,acc.year_of_birth,acc.xmluid,'accounts'::text AS tb_nm    FROM (accounts acc LEFT JOIN hid_phr_address acc_phr ON (((acc.health_id_number)::text = (acc_phr.health_id_number)::text))) where acc.mobile = :mobileNumber AND acc.status='ACTIVE'")
	Flux<Identity> findUsersByMobileNumber(String mobileNumber);

	@Query(value = "SELECT DISTINCT acc.health_id_number AS abha_number,acc_phr.phr_address AS abha_address FROM (accounts acc LEFT JOIN hid_phr_address acc_phr ON (((acc.health_id_number)::text = (acc_phr.health_id_number)::text))) where acc.mobile = :mobileNumber AND acc.status='ACTIVE'")
	Flux<Identity> findAllAbhaAddressByMobileNumber(String mobileNumber);

	@Query(value = "SELECT acc.health_id_number AS abha_number,acc_phr.phr_address AS abha_address FROM (accounts acc LEFT JOIN hid_phr_address acc_phr ON (((acc.health_id_number)::text = (acc_phr.health_id_number)::text))) where acc_phr.phr_address = :abhaAddress order by created_date desc limit 1")
	Mono<Identity> findUserWithoutStatusByAbhaAddress(String abhaAddress);

	@Query(value = "SELECT acc_phr.phr_address AS abha_address FROM (accounts acc LEFT JOIN hid_phr_address acc_phr ON (((acc.health_id_number)::text = (acc_phr.health_id_number)::text))) where acc_phr.phr_address in (:abhaAddress)")
	Flux<Identity> findUserByAbhaAddressList(List<String> abhaAddressList);

}
