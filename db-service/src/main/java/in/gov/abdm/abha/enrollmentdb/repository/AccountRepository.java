package in.gov.abdm.abha.enrollmentdb.repository;

import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public interface AccountRepository extends R2dbcRepository<Accounts, String> {
    @Query(value = "INSERT INTO accounts (health_id_number, address, origin, created_date, day_of_birth, district_code, district_name, email, facility_id, first_name, gender, health_id, kycdob, kyc_photo, profile_photo, kyc_verified, last_name, middle_name, mobile, month_of_birth, name, okyc_verified, password, pincode, state_code, state_name, status, update_date, hip_id, xmluid , year_of_birth, consent_date, profile_photo_compressed, email_verification_date, email_verified, document_code, verification_status, verification_type, lst_updated_by, consent_version, cm_migrated, phr_migrated, health_worker_mobile,health_worker_name,mobile_type, type, source, api_version, api_end_point,localized_details) VALUES (:#{#account.healthIdNumber},:#{#account.address},:#{#account.origin},:#{#account.createdDate},:#{#account.dayOfBirth},:#{#account.districtCode},:#{#account.districtName},:#{#account.email},:#{#account.facilityId},:#{#account.firstName},:#{#account.gender},:#{#account.healthId},:#{#account.kycdob},lo_from_bytea(0, convert_to(:#{#account.kycPhoto}, 'utf8')),lo_from_bytea(0, convert_to(:#{#account.profilePhoto}, 'utf8')),:#{#account.kycVerified },:#{#account.lastName},:#{#account.middleName},:#{#account.mobile},:#{#account.monthOfBirth},:#{#account.name},:#{#account.okycVerified},:#{#account.password},:#{#account.pincode},:#{#account.stateCode},:#{#account.stateName},:#{#account.status},:#{#account.updateDate},:#{#account.hipId},:#{#account.xmluid},:#{#account.yearOfBirth},:#{#account.consentDate},:#{#account.profilePhotoCompressed},:#{#account.emailVerificationDate},:#{#account.emailVerified},:#{#account.documentCode},:#{#account.verificationStatus},:#{#account.verificationType},:#{#account.lstUpdatedBy},:#{#account.consentVersion},:#{#account.cmMigrated},:#{#account.phrMigrated},:#{#account.healthWorkerMobile},:#{#account.healthWorkerName},:#{#account.mobileType},:#{#account.type},:#{#account.source},:#{#account.apiVersion},:#{#account.apiEndPoint},:#{#account.localizedDetails})")
    Mono<Accounts> saveAccounts(@Param("account") Accounts accounts);

    @Query(value = "UPDATE accounts SET health_id_number=:#{#account.healthIdNumber}, address=:#{#account.address}, origin=:#{#account.origin}, created_date=:#{#account.createdDate}, day_of_birth=:#{#account.dayOfBirth}, district_code=:#{#account.districtCode}, district_name=:#{#account.districtName}, email=:#{#account.email},facility_id=:#{#account.facilityId}, first_name=:#{#account.firstName}, gender=:#{#account.gender}, health_id=:#{#account.healthId}, kycdob=:#{#account.kycdob}, kyc_photo=lo_from_bytea(0, convert_to(:#{#account.kycPhoto}, 'utf8')), profile_photo=lo_from_bytea(0, convert_to(:#{#account.profilePhoto}, 'utf8')), kyc_verified=:#{#account.kycVerified}, last_name=:#{#account.lastName}, middle_name=:#{#account.middleName}, mobile=:#{#account.mobile}, month_of_birth=:#{#account.monthOfBirth}, name=:#{#account.name}, okyc_verified=:#{#account.okycVerified}, password=:#{#account.password}, pincode=:#{#account.pincode}, state_code=:#{#account.stateCode}, state_name=:#{#account.stateName}, status=:#{#account.status}, update_date=:#{#account.updateDate}, hip_id=:#{#account.hipId}, xmluid=:#{#account.xmluid}, year_of_birth=:#{#account.yearOfBirth}, consent_date=:#{#account.consentDate}, profile_photo_compressed=:#{#account.profilePhotoCompressed}, email_verification_date=:#{#account.emailVerificationDate}, email_verified=:#{#account.emailVerified}, document_code=:#{#account.documentCode}, verification_status=:#{#account.verificationStatus}, verification_type=:#{#account.verificationType}, lst_updated_by=:#{#account.lstUpdatedBy}, consent_version=:#{#account.consentVersion}, cm_migrated=:#{#account.cmMigrated},phr_migrated=:#{#account.phrMigrated},health_worker_mobile=:#{#account.healthWorkerMobile},health_worker_name=:#{#account.healthWorkerName},mobile_type=:#{#account.mobileType}, type=:#{#account.type}, localized_details=:#{#account.localizedDetails} where health_id_number = :healthIdNumber")
    Mono<Accounts> updateAccounts(@Param("healthIdNumber") String healthIdNumber, @Param("account") Accounts accounts);

    @Query(value = "SELECT * FROM fn_get_accounts_by_health_id_number(:healthIdNumber::text)")
    Mono<Accounts> getAccountsByHealthIdNumber(@Param("healthIdNumber") String healthIdNumber);

    @Query(value = "SELECT health_id_number, address, origin, created_date, day_of_birth, district_code, district_name, email, facility_id, first_name, gender, health_id, kycdob, encode(lo_get(kyc_photo), 'escape') as kyc_photo, kyc_verified, last_name, middle_name, mobile, month_of_birth, name, okyc_verified, password, pincode, encode(lo_get(profile_photo), 'escape') as profile_photo, state_code, state_name, status, sub_district_code, subdistrict_name, town_code, town_name, update_date, village_code, village_name, ward_code, ward_name, hip_id, xmluid as xmlUID, year_of_birth, consent_date, profile_photo_compressed, email_verification_date, email_verified, document_code, verification_status, verification_type, lst_updated_by, consent_version, cm_migrated, phr_migrated, health_worker_mobile, health_worker_name, mobile_type, type, mig_denom, lo_get(profile_photo) as comp_photo FROM accounts a where a.health_id_number in (:healthIdNumbers)")
    Flux<Accounts> getAccountsByHealthIdNumbers(@Param("healthIdNumber") List<String> healthIdNumbers);

    @Query(value = "SELECT * from fn_get_account_by_xmluid(:xmluid::text)")
    Mono<Accounts> getAccountsByXmluid(@Param("xmluid") String xmluid);

    @Query(value = "SELECT * FROM fn_get_accounts_by_document_code(:documentCode::text)")
    Mono<Accounts> getAccountByDocumentCode(@Param("documentCode") String documentCode);

    @Query(value = "SELECT * FROM fn_get_accounts_by_document_code(:documentCode::text)")
    Flux<Accounts> getAccountsByDocumentCode(@Param("documentCode") String documentCode);

    @Query(value = "select count from fn_get_accounts_cnt_by_mobile(:mobileNumber)")
    Mono<Integer> getAccountsCountByMobileNumber(String mobileNumber);

    @Query(value = "select count(health_id_number) from accounts where email_verified = :email and (status ='ACTIVE' or status ='DEACTIVATED');")
    Mono<Integer> getAccountsCountByEmailNumber(String email);

    @Query(value = "SELECT health_id_number, address, origin, created_date, day_of_birth, district_code, district_name, email, facility_id, first_name, gender, health_id, kycdob, encode(lo_get(kyc_photo), 'escape') as kyc_photo, kyc_verified, last_name, middle_name, mobile, month_of_birth, name, okyc_verified, password, pincode, encode(lo_get(profile_photo), 'escape') as profile_photo, state_code, state_name, status, sub_district_code, subdistrict_name, town_code, town_name, update_date, village_code, village_name, ward_code, ward_name, hip_id, xmluid as xmlUID, year_of_birth, consent_date, profile_photo_compressed, email_verification_date, email_verified, document_code, verification_status, verification_type, lst_updated_by, consent_version, cm_migrated, phr_migrated, health_worker_mobile, health_worker_name, mobile_type, type, mig_denom FROM accounts a where lower(a.first_name) = :firstName AND lower(a.last_name) = :lastName AND CAST (a.day_of_birth AS INTEGER) = :dob AND CAST (a.month_of_birth AS INTEGER) = :mob AND CAST (a.year_of_birth AS INTEGER) = :yob AND a.gender = :gender AND a.status != 'DELETED'")
    Mono<Accounts> checkDeDuplication(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("dob") Integer dob ,@Param("mob") Integer mob,@Param("yob") Integer yob,@Param("gender") String gender);

}