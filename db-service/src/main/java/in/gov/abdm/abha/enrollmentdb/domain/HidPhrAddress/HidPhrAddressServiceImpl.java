package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress;


import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event.PHREventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event.PatientEventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.account.AccountService;
import in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement.SyncAcknowledgementService;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.address.Address;
import in.gov.abdm.phr.enrollment.user.User;
import in.gov.abdm.syncacknowledgement.SyncAcknowledgement;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.*;

/**
 * A class which implements Business logic.
 */

@Service
@Slf4j
public class HidPhrAddressServiceImpl implements HidPhrAddressService {

    private static final String PROVISIONAL = "PROVISIONAL";

    @Autowired
    HidPhrAddressRepository hidPhrAddressRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PHREventPublisher phrEventPublisher;

    @Autowired
    private PatientEventPublisher patientEventPublisher;

    @Autowired
    private SyncAcknowledgementService syncAcknowledgementService;

    /**
     * Here we are creating a ModelMapper object and putting into IOC
     * for implementing singleton, with the reference all its methods can be utilized.
     */
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HidPhrAddressSubscriber hidPhrAddressSubscriber;

    @Override
    public Mono<HidPhrAddressDto> addHidPhrAddress(HidPhrAddressDto hidPhrAddressDto) {
        String requestId = String.valueOf(UUID.randomUUID());
        Timestamp timeStamp = Timestamp.valueOf(LocalDateTime.now());
        HidPhrAddress hidPhrAddress = modelMapper.map(hidPhrAddressDto, HidPhrAddress.class).setAsNew();
        return hidPhrAddressRepository.save(hidPhrAddress)
                .flatMap(hidPhrAddressAdded -> {
                    SyncAcknowledgement syncAcknowledgement = new SyncAcknowledgement();
                    syncAcknowledgement.setRequestID(requestId);
                    syncAcknowledgement.setHealthIdNumber(hidPhrAddressAdded.getHealthIdNumber());
                    syncAcknowledgement.setHidPhrAddress(hidPhrAddressAdded.getPhrAddress());
                    syncAcknowledgement.setSyncedWithPatient(false);
                    syncAcknowledgement.setSyncedWithPhr(false);
                    syncAcknowledgement.setCreatedDate(timeStamp);
                    return Mono.just(hidPhrAddressAdded);
                })
                .flatMap(this::findAccountFromHidPhrAddress)
                .flatMap(accountToPublish -> {
                    if (!accountToPublish.getVerificationStatus().equalsIgnoreCase(PROVISIONAL)) {
                        User userToPublish = setUserToPublish(accountToPublish);
                        Patient patientToPublish = setPatientToPublish(accountToPublish);
                        phrEventPublisher.publish(userToPublish.setAsNew(true), requestId);
                        patientEventPublisher.publish(patientToPublish.setNew(true), requestId);
                    }
                    return Mono.just(accountToPublish.getHidPhrAddress());
                })
                .map(hidPhrAdd -> modelMapper.map(hidPhrAdd, HidPhrAddressDto.class));
    }

    @Override
    public Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId) {
        HidPhrAddress hidPhrAddress = modelMapper.map(hidPhrAddressDto, HidPhrAddress.class);
        return hidPhrAddressRepository.save(hidPhrAddress)
                .map(hidPhrAdd -> modelMapper.map(hidPhrAdd, HidPhrAddressDto.class));
    }

    @Override
    public Mono<HidPhrAddressDto> getHidPhrAddressById(Long hidPhrAddressId) {
        return hidPhrAddressRepository.findById(hidPhrAddressId).
                map(HidPhrAddress -> modelMapper.map(HidPhrAddress, HidPhrAddressDto.class));
    }

    @Override
    public Mono<Void> deleteHidPhrAddressById(Long hidPhrAddressId) {
        return hidPhrAddressRepository.deleteById(hidPhrAddressId);
    }

    @Override
    public Flux<HidPhrAddressDto> getHidPhrAddressByHealthIdNumbersAndPreferredIn(List<String> healthIdNumbers,
                                                                                  List<Integer> preferred) {
        return hidPhrAddressRepository.findByHealthIdNumberInAndPreferredIn(healthIdNumbers, preferred)
                .map(hidPhrAdd -> modelMapper.map(hidPhrAdd, HidPhrAddressDto.class));
    }
    @Override
    public Flux<HidPhrAddressDto> findByPhrAddressIn(List<String> phrAddress) {
        return hidPhrAddressRepository.findByPhrAddressIn(phrAddress)
                .map(hidPhrAddress -> modelMapper.map(hidPhrAddress,HidPhrAddressDto.class));
    }

    @Override
    public Mono<HidPhrAddressDto> getPhrAddressByPhrAddress(String phrAddress) {
        return hidPhrAddressRepository.getPhrAddressByPhrAddress(phrAddress)
                .map(hidPhrAddress -> modelMapper.map(hidPhrAddress,HidPhrAddressDto.class));
    }

    @Override
    public Mono<HidPhrAddressDto> findByHealthIdNumber(String healthIdNumber) {
        return hidPhrAddressRepository.findByHealthIdNumber(healthIdNumber)
                .map(hidPhrAddress -> modelMapper.map(hidPhrAddress,HidPhrAddressDto.class));
    }

    private Mono<Accounts> findAccountFromHidPhrAddress (HidPhrAddress hidPhrAddress) {
        return accountService.getAccountByHealthIdNumber(hidPhrAddress.getHealthIdNumber())
                .map(accountsDto -> modelMapper.map(accountsDto, Accounts.class))
                .flatMap(account -> {
                    account.setHidPhrAddress(hidPhrAddress);
                    return Mono.just(account);
                });
    }

    private User setUserToPublish(Accounts accounts) {
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
            address.setDistrictName(accounts.getSubdistrictName());
            address.setTownCode(accounts.getTownCode());
            address.setTownName(accounts.getTownName());
            address.setVillageCode(accounts.getVillageCode());
            address.setVillageName(accounts.getVillageName());
            address.setWardCode(accounts.getWardCode());
            address.setWardName(accounts.getWardName());
            address.setCreatedBy(ABHA_SYNC);
            address.setUpdatedBy(ABHA_SYNC);

            user.setHealthIdNumber(accounts.getHealthIdNumber());
            if (null != accounts.getCreatedDate()) {
                LocalDateTime localDateTime = accounts.getCreatedDate();
               }
            user.setDayOfBirth(accounts.getDayOfBirth());
            user.setEmailId(accounts.getEmail());
            user.setFirstName(accounts.getFirstName());
            user.setGender(accounts.getGender());
            user.setProfilePhoto(accounts.getKycPhoto());
            user.setLastName(accounts.getLastName());
            user.setMiddleName(accounts.getMiddleName());
            user.setMobileNumber(accounts.getMobile());
            user.setMobileNumberVerified(accounts.getMobile()!=null);
            user.setMonthOfBirth(accounts.getMonthOfBirth());
            user.setFullName(accounts.getName());
            user.setPassword(accounts.getPassword());
            user.setStatus(accounts.getStatus());
            if(null != accounts.getUpdateDate()) {
                LocalDateTime localDateTime = accounts.getUpdateDate();
               }
            user.setYearOfBirth(accounts.getYearOfBirth());
            user.setDateOfBirth(accounts.getDayOfBirth() + "-" + accounts.getMonthOfBirth() + "-" + accounts.getYearOfBirth());
            user.setProfilePhotoCompressed(accounts.isProfilePhotoCompressed());
            user.setEmailIdVerified(false); // Email has to be verified at PHR system
            user.setUpdatedBy(accounts.getLstUpdatedBy());
            user.setCreatedBy(ABHA_SYNC);
            user.setUpdatedBy(ABHA_SYNC);
            user.setPhrAddress(accounts.getHidPhrAddress().getPhrAddress());
            user.setUserAddress(address);
            user.setKycStatus(accounts.isKycVerified() ? VERIFIED : "NOT VERIFIED");
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return user;
    }

    private Patient setPatientToPublish(Accounts accounts) {
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
            patient.setStatus(accounts.getStatus());
            if(null != accounts.getCreatedDate()) {
                LocalDateTime localDateTime = accounts.getCreatedDate();
                }
            if(null != accounts.getUpdateDate()) {
                LocalDateTime localDateTime = accounts.getUpdateDate();
             }
            patient.setEmailId(accounts.getEmail());
            patient.setAdd1(accounts.getAddress());
            patient.setPinCode(accounts.getPincode());
            patient.setEmailVerified(null != accounts.getEmailVerified());
            patient.setKycStatus(accounts.isKycVerified() ? VERIFIED : PENDING);
            patient.setMobileVerified(accounts.getMobile()!=null);
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return patient;
    }
}
