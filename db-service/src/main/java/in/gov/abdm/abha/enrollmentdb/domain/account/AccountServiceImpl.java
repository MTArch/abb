package in.gov.abdm.abha.enrollmentdb.domain.account;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event.PHREventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event.PatientEventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement.SyncAcknowledgementService;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.address.Address;
import in.gov.abdm.phr.enrollment.user.User;
import in.gov.abdm.syncacknowledgement.SyncAcknowledgement;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.repository.AccountRepository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private SyncAcknowledgementService syncAcknowledgementService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountSubscriber accountSubscriber;

    @Autowired
    private PHREventPublisher phrEventPublisher;

    @Autowired
    private PatientEventPublisher patientEventPublisher;
    @Autowired
    HidPhrAddressRepository hidPhrAddressRepository;

    private static final String DRIVING_LICENCE = "DRIVING_LICENCE";

    @Override
    public Mono<AccountDto> addAccount(AccountDto accountDto) {
        Accounts account = map(accountDto);
        return accountRepository.saveAccounts(account.setAsNew())
                .map(accounts -> modelMapper.map(account, AccountDto.class))
                // TODO ANAND
//                .onErrorResume(throwable -> log.error(throwable.getMessage()))
                .switchIfEmpty(Mono.just(accountDto));
    }

    @Override
    public Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber) {
        return accountRepository.getAccountsByHealthIdNumber(healthIdNumber).map(accounts -> modelMapper.map(accounts, AccountDto.class));
    }

    @Override
    public Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber) {
        String requestId = String.valueOf(UUID.randomUUID());
        Timestamp timeStamp = Timestamp.valueOf(LocalDateTime.now());
        Accounts account = map(accountDto);
        account.setNewAccount(false);
        return accountRepository.updateAccounts(account.getHealthIdNumber(), account)
                .map(accounts -> modelMapper.map(account, AccountDto.class))
//                .onErrorResume(throwable -> log.error(throwable.getMessage()))
                .switchIfEmpty(Mono.just(accountDto))
                .flatMap(accountUpdated -> {
                    SyncAcknowledgement syncAcknowledgement = new SyncAcknowledgement();
                    syncAcknowledgement.setRequestID(requestId);
                    syncAcknowledgement.setHealthIdNumber(accountUpdated.getHealthIdNumber());
                    syncAcknowledgement.setSyncedWithPatient(false);
                    syncAcknowledgement.setSyncedWithPhr(false);
                    syncAcknowledgement.setCreatedDate(timeStamp);
//                    syncAcknowledgementService.addNewAcknowledgement(requestId, timeStamp, syncAcknowledgement); //TODO - Uncomment the logic to save the sync acknowledgement object after table creation
                    return Mono.just(accountUpdated);
                })
                .flatMap(this::findHidPhrAddressFromAccount)
                .flatMap(accountToBePublished -> {
                    User userToBePublished =null;
                    Patient patientToBePublished = null;
                    if (accountToBePublished.getVerificationType().equalsIgnoreCase(DRIVING_LICENCE)) {
                        userToBePublished = setUserToPublish(accountToBePublished);
                        patientToBePublished = setPatientToPublish(accountToBePublished);
                        phrEventPublisher.publish(userToBePublished.setAsNew(true), requestId);
                        patientEventPublisher.publish(patientToBePublished.setNew(true), requestId);
                    } else {
                        userToBePublished = mapAccountToUser(accountToBePublished);
                        patientToBePublished = mapAccountToPatient(accountToBePublished);
                        phrEventPublisher.publish(userToBePublished.setAsNew(false), requestId);
                        patientEventPublisher.publish(patientToBePublished.setNew(false), requestId);
                    }
                    return Mono.just(accountToBePublished);
                })
                .flatMap(accounts -> Mono.just(modelMapper.map(account, AccountDto.class)));
    }

    @Override
    public Mono getAccountByXmlUid(String xmluid) {
        return accountRepository.getAccountsByXmluid(xmluid);
    }

    @Override
    public Flux<AccountDto> getAccountsByHealthIdNumbers(List<String> healthIdNumbers) {
        return accountRepository.getAccountsByHealthIdNumbers(healthIdNumbers).map(account -> modelMapper.map(account, AccountDto.class));
    }

    @Override
    public Mono<AccountDto> getAccountByDocumentCode(String documentCode) {
        return accountRepository.getAccountsByDocumentCode(documentCode).map(account -> modelMapper.map(account, AccountDto.class));
    }

    @Override
    public Mono<Integer> getMobileLinkedAccountsCount(String mobileNumber) {
        return accountRepository.getAccountsCountByMobileNumber(mobileNumber);
    }

    private Accounts map(AccountDto accountDto){
        Accounts account = modelMapper.map(accountDto, Accounts.class);
        if (accountDto.getKycPhoto() != null && accountDto.getKycPhoto().isEmpty()) {
            account.setKycPhoto(null);
        }
        if (accountDto.getProfilePhoto() != null && accountDto.getProfilePhoto().isEmpty()) {
            account.setProfilePhoto(null);
        }
        return account;
    }

    private User mapAccountToUser(AccountDto accountDto) {
        User userToBePublished = new User();
        userToBePublished.setHealthIdNumber(accountDto.getHealthIdNumber());
        userToBePublished.setMobileNumber(accountDto.getMobile());
        userToBePublished.setMobileNumberVerified(null != accountDto.getMobile());
        userToBePublished.setEmailId(accountDto.getEmail());
        userToBePublished.setEmailIdVerified(null != accountDto.getEmailVerified());
        userToBePublished.setUpdatedBy("ABHA_SYNC");
        userToBePublished.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
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
        return patientToBePublished;
    }

    private Mono<AccountDto> findHidPhrAddressFromAccount(AccountDto accountDto) {
        return hidPhrAddressRepository.getPhrAddressByPhrAddress(accountDto.getHealthId().toLowerCase())
                .flatMap(hidPhrAddress -> {
                    accountDto.setHidPhrAddress(hidPhrAddress);
                    return Mono.just(accountDto);
                }).switchIfEmpty(Mono.just(accountDto));
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
            address.setCreatedBy("ABHA_SYNC");
            address.setUpdatedBy("ABHA_SYNC");

            user.setHealthIdNumber(accounts.getHealthIdNumber());
            if (null != accounts.getCreatedDate()) {
                LocalDateTime localDateTime = accounts.getCreatedDate();
                //TODO - Correct the below logic to enter correct date, else leave it blank as user table has default values for this entry.
//                user.setCreatedDate(new Timestamp(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond(), localDateTime.getNano()));
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
            user.setPassword(accounts.getPassword()); //TODO - Verify if password can be reused or not
            user.setStatus(accounts.getStatus());
            if(null != accounts.getUpdateDate()) {
                LocalDateTime localDateTime = accounts.getUpdateDate();
                //TODO - Correct the below logic to enter correct date, else leave it blank as user table has default values for this entry.
//                user.setUpdatedDate(new Timestamp(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond(), localDateTime.getNano()));
            }
            user.setYearOfBirth(accounts.getYearOfBirth());
            user.setDateOfBirth(accounts.getDayOfBirth() + "-" + accounts.getMonthOfBirth() + "-" + accounts.getYearOfBirth());
            user.setProfilePhotoCompressed(accounts.isProfilePhotoCompressed());
            user.setEmailIdVerified(false); // Email has to be verified at PHR system
            user.setUpdatedBy(accounts.getLstUpdatedBy());
            user.setCreatedBy("ABHA_SYNC");
            user.setUpdatedBy("ABHA_SYNC");
            user.setPhrAddress(accounts.getHidPhrAddress().getPhrAddress());
            user.setUserAddress(address);
            user.setKycStatus(accounts.isKycVerified() ? "VERIFIED" : "NOT VERIFIED"); //TODO: Move the hard coded values to constants
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return user;
    }

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
            patient.setStatus(accounts.getStatus());
            if(null != accounts.getCreatedDate()) {
                LocalDateTime localDateTime = accounts.getCreatedDate();
                //TODO - Correct the below logic to enter correct date.
//                patient.setDateCreated(new Timestamp(localDateTime.getYear()-1900, localDateTime.getMonthValue()+1, localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond(), localDateTime.getNano()));
            }
            if(null != accounts.getUpdateDate()) {
                LocalDateTime localDateTime = accounts.getUpdateDate();
                //TODO - Correct the below logic to enter correct date.
//                patient.setDateModified(new Timestamp(localDateTime.getYear()-1900, localDateTime.getMonthValue()+1, localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond(), localDateTime.getNano()));
            }
            patient.setEmailId(accounts.getEmail());
            patient.setAdd1(accounts.getAddress());
            patient.setPinCode(accounts.getPincode());
//            patient.setKycVerified(accounts.isKycVerified()); //TODO: Uncomment the code once kyc_verified column is added in patient table of sandbox.
            patient.setEmailVerified(null != accounts.getEmailVerified());
            patient.setKycStatus(accounts.isKycVerified() ? "VERIFIED" : "PENDING");
            patient.setMobileVerified(accounts.getMobile()!=null);
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return patient;
    }

}
