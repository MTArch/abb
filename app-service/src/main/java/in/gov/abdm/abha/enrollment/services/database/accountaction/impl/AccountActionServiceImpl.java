package in.gov.abdm.abha.enrollment.services.database.accountaction.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountActionFClient;
import in.gov.abdm.abha.enrollment.model.entities.AccountActionDto;
import in.gov.abdm.abha.enrollment.services.database.accountaction.AccountActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountActionServiceImpl implements AccountActionService {

    @Autowired
    AbhaDBAccountActionFClient abhaDBAccountActionFClient;

    @Override
    public Mono<AccountActionDto> createAccountActionEntity(AccountActionDto accountActionDto) {
        accountActionDto.setNewAccount(true);
        return abhaDBAccountActionFClient.postAccountAction(accountActionDto);
    }


}
