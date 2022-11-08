package in.gov.abdm.abha.enrollmentdb.domain.account;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountSubscriber;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	private ModelMapper modelMapper;

    @Autowired
    private AccountSubscriber accountSubscriber;

	@Override
	public Mono<Accounts> addAccount(AccountDto accountDto) {
		Accounts account = modelMapper.map(accountDto, Accounts.class);
		return accountRepository.save(account);	
	}

	@Override
	public Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber) {
		return accountRepository.findById(healthIdNumber).map(account -> modelMapper.map(account, AccountDto.class));
	}

	@Override
	public Mono<Accounts> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber) {
		accountDto.setNewAccount(false);
		Accounts account = modelMapper.map(accountDto, Accounts.class);
		return accountRepository.save(account);	
	}

	@Override
	public Mono<AccountDto> getAccountByXmlUid(String xmluid) {
		return accountRepository.findByxmluid(xmluid).map(account -> modelMapper.map(account,AccountDto.class));
	}

}
