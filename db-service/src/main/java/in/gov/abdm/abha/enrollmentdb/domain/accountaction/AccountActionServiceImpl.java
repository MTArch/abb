package in.gov.abdm.abha.enrollmentdb.domain.accountaction;

import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import in.gov.abdm.abha.enrollmentdb.repository.AccountActionRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountActionServiceImpl implements AccountActionService {

    @Autowired
    AccountActionRepository accountActionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountActionSubscriber accountSubscriber;


    @Override
    public Mono<AccountActions> updateAccountActionByHealthIdNumber(AccountActionDto accountActionDto, String healthIdNumber) {
        AccountActions accountAction = map(accountActionDto);
        accountAction.setNewAccount(false);
        return accountActionRepository.updateAccountAction(accountAction.getHealthIdNumber(), accountAction);
//                .map(accountActions -> modelMapper.map(accountAction, AccountActionDto.class))
//                .doOnError(throwable -> log.error(throwable.getMessage()))
//                .switchIfEmpty(Mono.just(accountActionDto));
    }

    @Override
    public Mono<AccountActionDto> getAccountActionByHealthIdNumber(String healthIdNumber) {
        return accountActionRepository.getAccountsByHealthIdNumber(healthIdNumber);
    }

    @Override
    public Mono<AccountActionDto> addAccount(AccountActionDto accountActionDto) {
//        AccountActions accountActions = map(accountActionDto);
        accountActionDto.setNewAccount(true);
        return accountActionRepository.saveAccounts(accountActionDto)
                .map(accounts -> modelMapper.map(accountActionDto, AccountActionDto.class))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .switchIfEmpty(Mono.just(accountActionDto));
    }


    private AccountActions map(AccountActionDto accountActionDto) {
        return modelMapper.map(accountActionDto, AccountActions.class);
    }
}
