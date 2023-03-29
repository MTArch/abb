package in.gov.abdm.abha.enrollmentdb.domain.account;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event.PHREventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event.PatientEventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement.SyncAcknowledgementService;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
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
                .flatMap(accountToBePublished -> {
                    User userToBePublished = mapAccountToUser(accountToBePublished);
                    Patient patientToBePublished = mapAccountToPatient(accountToBePublished);
                    phrEventPublisher.publish(userToBePublished.setAsNew(false), requestId);
                    patientEventPublisher.publish(patientToBePublished.setNew(false), requestId);
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

    @Override
    public Mono<Integer> getEmailLinkedAccountsCount(String email) {
        return accountRepository.getAccountsCountByEmailNumber(email);
    }
}
