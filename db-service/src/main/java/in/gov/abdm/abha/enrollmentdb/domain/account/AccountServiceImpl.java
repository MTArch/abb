package in.gov.abdm.abha.enrollmentdb.domain.account;

import in.gov.abdm.abha.enrollmentdb.domain.kafka.KafkaService;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.de_duplication.DeDuplicationRequest;
import in.gov.abdm.abha.enrollmentdb.repository.AccountRepository;
import in.gov.abdm.abha.enrollmentdb.utilities.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    KafkaService kafkaService;

    @Override
    public Mono<AccountDto> addAccount(AccountDto accountDto) {
        Accounts account = map(accountDto);
        return accountRepository.saveAccounts(account.setAsNew())
                .map(accounts -> modelMapper.map(account, AccountDto.class))
                .switchIfEmpty(Mono.just(accountDto));
    }

    @Override
    public Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber) {
        return accountRepository.getAccountsByHealthIdNumber(healthIdNumber)
                .flatMap(this::deCompressProfilePhoto)
                .map(accounts -> modelMapper.map(accounts, AccountDto.class));
    }

    @Override
    public Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber) {
        Accounts account = map(accountDto);
        account.setNewAccount(false);
        return accountRepository.updateAccounts(account.getHealthIdNumber(), account)
                .map(accounts -> modelMapper.map(account, AccountDto.class))
                .switchIfEmpty(Mono.just(accountDto))
                .flatMap(accounts ->{
                    kafkaService.publishPhrUserPatientEventByAccounts(accountDto).subscribe();
                    return  Mono.just(modelMapper.map(account, AccountDto.class));
                });
    }

    @Override
    public Mono<AccountDto> getAccountByXmlUid(String xmluid) {
        return accountRepository.getAccountsByXmluid(xmluid)
                .flatMap(this::deCompressProfilePhoto)
                .map(accounts -> modelMapper.map(accounts, AccountDto.class));
    }

    @Override
    public Flux<AccountDto> getAccountsByHealthIdNumbers(List<String> healthIdNumbers) {
        return accountRepository.getAccountsByHealthIdNumbers(healthIdNumbers)
                .flatMap(this::deCompressProfilePhoto)
                .map(account -> modelMapper.map(account, AccountDto.class));
    }

    @Override
    public Mono<AccountDto> getAccountByDocumentCode(String documentCode) {
        return accountRepository.getAccountsByDocumentCode(documentCode)
                .flatMap(this::deCompressProfilePhoto)
                .map(account -> modelMapper.map(account, AccountDto.class));
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
    @Override
    public Mono<Integer> getEmailLinkedAccountsCount(String email) {
        return accountRepository.getAccountsCountByEmailNumber(email);
    }

    @Override
    public Mono<AccountDto> checkDeDuplication(DeDuplicationRequest request) {
        return accountRepository.checkDeDuplication(request.getFirstName(),request.getLastName(),request.getDob(),request.getMob(),request.getYob(),request.getGender())
                .map(account -> modelMapper.map(account, AccountDto.class));
    }

    private Mono<Accounts> deCompressProfilePhoto(Accounts account){
        if(account.isProfilePhotoCompressed()){
            account.setProfilePhoto(new String(ImageUtils.decompress(account.getCompPhoto())));
        }
        return Mono.just(account);
    }
}
