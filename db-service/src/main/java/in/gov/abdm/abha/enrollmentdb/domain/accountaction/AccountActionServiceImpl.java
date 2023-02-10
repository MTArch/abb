package in.gov.abdm.abha.enrollmentdb.domain.accountaction;

import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import in.gov.abdm.abha.enrollmentdb.repository.AccountActionRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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
    public Mono<AccountActionDto> getAccountActionByHealthIdNumber(String healthIdNumber) {
        return accountActionRepository.getAccountsByHealthIdNumber(healthIdNumber);
    }

    @Override
    public Mono<AccountActions> addAccount(AccountActions accountActions) {
        accountActions.setCreatedDate(LocalDateTime.now());
        accountActions.setAsNew();
        return accountActionRepository.save(accountActions);
    }


    private AccountActions map(AccountActionDto accountActionDto) {
        return modelMapper.map(accountActionDto, AccountActions.class);
    }
}
