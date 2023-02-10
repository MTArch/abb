package in.gov.abdm.abha.enrollmentdb.domain.account;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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
    private ModelMapper modelMapper;

    @Autowired
    private AccountSubscriber accountSubscriber;

    @Override
    public Mono<AccountDto> addAccount(AccountDto accountDto) {
        Accounts account = map(accountDto);
        return accountRepository.saveAccounts(account.setAsNew())
                .map(accounts -> modelMapper.map(account, AccountDto.class))
                .onErrorResume(throwable -> log.error(throwable.getMessage()))
                .switchIfEmpty(Mono.just(accountDto));
    }

    @Override
    public Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber) {
        return accountRepository.getAccountsByHealthIdNumber(healthIdNumber).map(accounts -> modelMapper.map(accounts, AccountDto.class));
    }

    @Override
    public Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber) {
        Accounts account = map(accountDto);
        account.setNewAccount(false);
        return accountRepository.updateAccounts(account.getHealthIdNumber(), account)
                .map(accounts -> modelMapper.map(account, AccountDto.class))
                .onErrorResume(throwable -> log.error(throwable.getMessage()))
                .switchIfEmpty(Mono.just(accountDto));
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
}
