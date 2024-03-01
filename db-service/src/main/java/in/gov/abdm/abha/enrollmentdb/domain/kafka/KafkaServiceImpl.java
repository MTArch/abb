package in.gov.abdm.abha.enrollmentdb.domain.kafka;

import static in.gov.abdm.abha.constant.ABHAConstants.DRIVING_LICENCE;
import static in.gov.abdm.abha.constant.ABHAConstants.VERIFIED;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.AADHAAR;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ABHA_SYNC;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.DL;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.KAFKA_ERROR_LOG_MSG;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.PROVISIONAL;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.SYSTEM;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.constant.StringConstants;
import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.DashboardEventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.PHREventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.PatientEventPublisher;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.repository.AccountRepository;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.address.Address;
import in.gov.abdm.phr.enrollment.user.User;
import in.gov.abdm.syncacknowledgement.SyncAcknowledgement;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService{

    @Autowired
    HidPhrAddressRepository hidPhrAddressRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PHREventPublisher phrEventPublisher;
    @Autowired
    PatientEventPublisher patientEventPublisher;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    DashboardEventPublisher dashboardEventPublisher;

    @Override
    public Mono<Void> publishPhrUserPatientEvent(HidPhrAddress hidPhrAddress){
        String requestId = String.valueOf(UUID.randomUUID());
        return accountRepository.getAccountsByHealthIdNumber(hidPhrAddress.getHealthIdNumber())
                .flatMap(account -> {
                    account.setHidPhrAddress(hidPhrAddress);
                    AccountDto accountDto = modelMapper.map(account, AccountDto.class);
                    saveSyncAcknowledgement(requestId, hidPhrAddress.getHealthIdNumber(),hidPhrAddress.getPhrAddress());
                    User userToPublish = setUserToPublish(accountDto);
                    Patient patientToPublish = setPatientToPublish(accountDto);
                    if (!accountDto.getVerificationStatus().equalsIgnoreCase(PROVISIONAL) && !accountDto.getHidPhrAddress().getStatus().equalsIgnoreCase(SYSTEM)) {
                        userToPublish.setCreatedBy(userToPublish.getCreatedBy() + (accountDto.getDocumentCode()==null ? StringConstants.HASH + AADHAAR : StringConstants.HASH + DL));
                        phrEventPublisher.publish(userToPublish.setAsNew(true), requestId);
                        patientEventPublisher.publish(patientToPublish.setNew(true), requestId);
                    }else{
                        phrEventPublisher.publish(userToPublish.setAsNew(false), requestId);
                        patientEventPublisher.publish(patientToPublish.setNew(false), requestId);
                    }
                    return Mono.empty();
                }).onErrorResume(e->{
                    log.error(KAFKA_ERROR_LOG_MSG ,e.getMessage());
                    return Mono.empty();
                }).then();

    }

    @Override
    public Mono<Void> publishPhrUserPatientEventByAccounts(AccountDto accountDto){
        String requestId = String.valueOf(UUID.randomUUID());
        return hidPhrAddressRepository.getPhrAddressByPhrAddress(accountDto.getHealthId())
                .flatMap(hidPhrAddress -> {
                    accountDto.setHidPhrAddress(hidPhrAddress);
                    User userToBePublished =null;
                    Patient patientToBePublished = null;
                    saveSyncAcknowledgement(requestId, hidPhrAddress.getHealthIdNumber(),hidPhrAddress.getPhrAddress());
                    if (accountDto.getVerificationType().equalsIgnoreCase(DRIVING_LICENCE) && accountDto.getVerificationStatus().equalsIgnoreCase(VERIFIED)) {
                        userToBePublished = setUserToPublish(accountDto);
                        userToBePublished.setCreatedBy(userToBePublished.getCreatedBy() + StringConstants.HASH + DL);
                        patientToBePublished = setPatientToPublish(accountDto);
                        phrEventPublisher.publish(userToBePublished.setAsNew(true), requestId);
                        patientEventPublisher.publish(patientToBePublished.setNew(true), requestId);
                    } else {
                        userToBePublished = mapAccountToUser(accountDto);
                        patientToBePublished = mapAccountToPatient(accountDto);
                        phrEventPublisher.publish(userToBePublished.setAsNew(false), requestId);
                        patientEventPublisher.publish(patientToBePublished.setNew(false), requestId);
                    }
                    return Mono.empty();
                }).onErrorResume(e->{
                    log.error(KAFKA_ERROR_LOG_MSG,e.getMessage());
                    return Mono.empty();
                }).then();
    }


    private void saveSyncAcknowledgement(String requestId, String healthIdNumber, String phrAddress) {
        Timestamp timeStamp = Timestamp.valueOf(LocalDateTime.now());
        SyncAcknowledgement syncAcknowledgement = new SyncAcknowledgement();
        syncAcknowledgement.setRequestID(requestId);
        syncAcknowledgement.setHealthIdNumber(healthIdNumber);
        syncAcknowledgement.setHidPhrAddress(phrAddress);
        syncAcknowledgement.setSyncedWithPatient(false);
        syncAcknowledgement.setSyncedWithPhr(false);
        syncAcknowledgement.setCreatedDate(timeStamp);
    }


    private User setUserToPublish(AccountDto accounts) {
        User user = new User();
        Address address = new Address();
        try {
            address.setAddressLine(accounts.getAddress());
            address.setDistrictCode(accounts.getDistrictCode());
            address.setDistrictName(accounts.getDistrictName());
            address.setPinCode(accounts.getPincode());
            address.setStateCode(accounts.getStateCode());
            address.setStateName(accounts.getStateName());
            address.setSubDistrictCode(accounts.getSubDistrictCode());
            address.setDistrictName(accounts.getSubDistrictName());
            address.setTownCode(accounts.getTownCode());
            address.setTownName(accounts.getTownName());
            address.setVillageCode(accounts.getVillageCode());
            address.setVillageName(accounts.getVillageName());
            address.setWardCode(accounts.getWardCode());
            address.setWardName(accounts.getWardName());
            address.setCreatedBy(ABHA_SYNC);
            address.setUpdatedBy(ABHA_SYNC);

            user.setHealthIdNumber(accounts.getHealthIdNumber());
            user.setDayOfBirth(accounts.getDayOfBirth()!=null?accounts.getDayOfBirth():EMPTY);
            user.setEmailId(accounts.getEmail());
            user.setFirstName(accounts.getFirstName());
            user.setGender(accounts.getGender());
            user.setProfilePhoto(accounts.getKycPhoto());
            user.setLastName(accounts.getLastName());
            user.setMiddleName(accounts.getMiddleName());
            user.setMobileNumber(accounts.getMobile());
            user.setMobileNumberVerified(accounts.getMobile()!=null);
            user.setMonthOfBirth(accounts.getMonthOfBirth()!=null?accounts.getMonthOfBirth():EMPTY);
            user.setFullName(accounts.getName());
            user.setPassword(accounts.getPassword());
            if(accounts.getHidPhrAddress().getStatus().equalsIgnoreCase(SYSTEM)){
                user.setStatus(SYSTEM);
            }else{
                user.setStatus(accounts.getStatus());
            }
            user.setYearOfBirth(accounts.getYearOfBirth()!=null?accounts.getYearOfBirth():EMPTY);
            if(!StringUtils.isEmpty(accounts.getDayOfBirth()) && !StringUtils.isEmpty(accounts.getMonthOfBirth()) && !StringUtils.isEmpty(accounts.getYearOfBirth())){
                user.setDateOfBirth(accounts.getDayOfBirth() + "-" + accounts.getMonthOfBirth() + "-" + accounts.getYearOfBirth());
            }else{
                user.setDateOfBirth(EMPTY);
            }
            user.setProfilePhotoCompressed(accounts.isProfilePhotoCompressed());
            user.setEmailIdVerified(false); // Email has to be verified at PHR system
            user.setUpdatedBy(accounts.getLstUpdatedBy());
            user.setCreatedBy(ABHA_SYNC);
            user.setUpdatedBy(ABHA_SYNC);
            user.setPhrAddress(accounts.getHidPhrAddress().getPhrAddress());
            user.setUserAddress(address);
            user.setKycStatus(accounts.isKycVerified() ? "VERIFIED" : "NOT VERIFIED");
        }
        catch (Exception ex) {
            log.error(ex.getMessage(),ex);
        }
        return user;
    }

    /**
     * Sets Patient object for publishing to PHR system
     *
     * @param accounts AccountDto object containing Account details
     * @return Patient object containing Patient details
     */
    private Patient setPatientToPublish(AccountDto accounts) {
        Patient patient = new Patient();
        try {
            patient.setId(accounts.getHidPhrAddress().getPhrAddress());
            patient.setFirstName(accounts.getFirstName());
            patient.setGender(accounts.getGender());
            patient.setPhoneNumber(accounts.getMobile());
            if(null != accounts.getYearOfBirth())
                patient.setYearOfBirth(BigInteger.valueOf(Integer.valueOf(accounts.getYearOfBirth())));
            if(null != accounts.getMonthOfBirth())
                patient.setMonthOfBirth(BigInteger.valueOf(Integer.valueOf(accounts.getMonthOfBirth())));
            if(null != accounts.getDayOfBirth())
                patient.setDateOfBirth(BigInteger.valueOf(Integer.valueOf(accounts.getDayOfBirth())));
            patient.setMiddleName(accounts.getMiddleName());
            patient.setLastName(accounts.getLastName());
            patient.setHealthIdNumber(accounts.getHealthIdNumber());
            patient.setStateCode(accounts.getStateCode());
            patient.setDistrictCode(accounts.getDistrictCode());
            if(accounts.getHidPhrAddress().getStatus().equalsIgnoreCase(SYSTEM))
                patient.setStatus(SYSTEM);
            else
                patient.setStatus(accounts.getStatus());
            patient.setEmailId(accounts.getEmail());
            patient.setAdd1(accounts.getAddress());
            patient.setPinCode(accounts.getPincode());
            patient.setEmailVerified(null != accounts.getEmailVerified());
            patient.setKycStatus(accounts.isKycVerified() ? "VERIFIED" : "PENDING");
            patient.setKycVerified(accounts.isKycVerified());
            patient.setMobileVerified(accounts.getMobile()!=null);
        }
        catch (Exception ex) {
            log.error(ex.getMessage(),ex);
        }
        return patient;
    }

    private User mapAccountToUser(AccountDto accountDto) {
        User userToBePublished = new User();
        userToBePublished.setHealthIdNumber(accountDto.getHealthIdNumber());
        userToBePublished.setMobileNumber(accountDto.getMobile());
        userToBePublished.setMobileNumberVerified(null != accountDto.getMobile());
        userToBePublished.setEmailId(accountDto.getEmail());
        userToBePublished.setEmailIdVerified(null != accountDto.getEmailVerified());
        userToBePublished.setUpdatedBy(ABHA_SYNC);
        userToBePublished.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        userToBePublished.setStatus(accountDto.getStatus());
        return userToBePublished;
    }

    private Patient mapAccountToPatient(AccountDto accountDto) {
        Patient patientToBePublished = new Patient();
        patientToBePublished.setHealthIdNumber(accountDto.getHealthIdNumber());
        patientToBePublished.setPhoneNumber(accountDto.getMobile());
        patientToBePublished.setMobileVerified(null != accountDto.getMobile());
        patientToBePublished.setEmailId(accountDto.getEmail());
        patientToBePublished.setEmailVerified(null != accountDto.getEmailVerified());
        patientToBePublished.setDateModified(Timestamp.valueOf(LocalDateTime.now()));
        patientToBePublished.setStatus(accountDto.getStatus());
        return patientToBePublished;
    }

    @Override
    public Mono<Void> publishDashBoardAbhaEventByAccounts(AccountReattemptDto rAccountDto) {
        String requestId = String.valueOf(UUID.randomUUID());
        dashboardEventPublisher.publish(rAccountDto, requestId);
        return Mono.empty();

    }
}
