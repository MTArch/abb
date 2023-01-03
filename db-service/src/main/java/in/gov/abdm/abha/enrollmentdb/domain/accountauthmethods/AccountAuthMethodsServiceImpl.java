package in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods;

import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollmentdb.repository.AccountAuthMethodsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;


/**
 * A class which implements Business logic.
 */
@Service
public class AccountAuthMethodsServiceImpl implements AccountAuthMethodsService {

    @Autowired
    AccountAuthMethodsRepository accountAuthMethodsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountAuthMethodsSubscriber accountAuthMethodSubscriber;

    @Override
    public Flux<AccountAuthMethodsDto> addAccountAuthMethods(List<AccountAuthMethods> accountAuthMethods) {
        accountAuthMethods.forEach(AccountAuthMethods::setAsNew);
        return accountAuthMethodsRepository.saveAll(accountAuthMethods).map(res -> modelMapper.map(res, AccountAuthMethodsDto.class));
    }
}
