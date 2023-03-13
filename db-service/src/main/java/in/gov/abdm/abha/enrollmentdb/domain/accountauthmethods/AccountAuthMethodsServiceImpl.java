package in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods;

import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollmentdb.repository.AccountAuthMethodsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
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
        List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
        accountAuthMethods.forEach(accountAuthMethods1 -> {
            accountAuthMethods1.setAsNew();
            accountAuthMethodsRepository.saveIfNotExist(accountAuthMethods1).subscribe();
            accountAuthMethodsDtos.add(modelMapper.map(accountAuthMethods1, AccountAuthMethodsDto.class));
        });
        return Flux.fromIterable(accountAuthMethodsDtos);

    }
}
