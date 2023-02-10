package in.gov.abdm.abha.enrollment.services.database.accountaction.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.model.entities.AccountActionDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.services.database.accountaction.AccountActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountActionServiceImpl extends AbhaDBClient implements AccountActionService{

    @Override
    public Mono<AccountActionDto> getAccountActionByHealthIdNumber(String healthIdNumber) {
        return getAccountEntityById(AccountActionDto.class, healthIdNumber);
    }

    @Override
    public Mono<AccountActionDto> createAccountActionEntity(AccountActionDto accountActionDto) {
        accountActionDto.setNewAccount(true);
        return addEntity(AccountActionDto.class, accountActionDto);
    }


}
