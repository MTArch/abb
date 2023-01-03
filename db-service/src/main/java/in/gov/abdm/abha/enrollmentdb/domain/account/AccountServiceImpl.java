package in.gov.abdm.abha.enrollmentdb.domain.account;

import java.util.Base64;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.abha.enrollmentdb.repository.AccountRepository;
import reactor.core.publisher.Flux;
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
	public Mono<AccountDto> addAccount(AccountDto accountDto) {
		Accounts account = modelMapper.map(accountDto, Accounts.class);
		return accountRepository.save(account).flatMap(acc -> updatePic(accountDto, acc));
	}

	private Mono<AccountDto> updatePic(AccountDto accountDto, Accounts account) {
		return accountRepository.updateKycPhoto(accountDto.getKycPhoto().getBytes(), account.getHealthIdNumber())
				.switchIfEmpty(Mono.defer(() -> {
					return Mono.just(accountDto);
				}));
	}

	@Override
	public Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber) {
		return accountRepository.findById(healthIdNumber).flatMap(account -> getPic(healthIdNumber, account));
	}

	private Mono<AccountDto> getPic(String healthIdNumber, Accounts account) {
		return accountRepository.getProfilePhoto(healthIdNumber).map(pic -> {
			AccountDto acc = modelMapper.map(account, AccountDto.class);
			acc.setKycPhoto(new String(Base64.getDecoder().decode(pic.replace("\n", ""))));
			return acc;
		}).switchIfEmpty(Mono.just(modelMapper.map(account, AccountDto.class)));
	}

	@Override
	public Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber) {
		accountDto.setNewAccount(false);
		Accounts account = modelMapper.map(accountDto, Accounts.class);
		return accountRepository.save(account).flatMap(acc -> updatePic(accountDto, account));
	}

	@Override
	public Mono<AccountDto> getAccountByXmlUid(String xmluid) {
		return accountRepository.findByXmluid(xmluid).flatMap(account -> getPic(account.getHealthIdNumber(), account));
	}

	@Override
	public Flux<AccountDto> getAccountsByHealthIdNumbers(List<String> healthIdNumbers) {
		return accountRepository.findByHealthIdNumberIn(healthIdNumbers)
				.flatMap(account -> getPic(account.getHealthIdNumber(), account));
	}
	@Override
	public Mono<AccountDto> getAccountByDocumentCode(String documentCode) {
		return accountRepository.findByDocumentCode(documentCode).map(account -> modelMapper.map(account,AccountDto.class));
	}

}
