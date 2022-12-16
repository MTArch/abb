package in.gov.abdm.abha.enrollmentdb.domain.account;

import java.util.List;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer to perform crud operations on Account Entity
 */
public interface AccountService {

    /**
     * to add new account
     *
     * @param accountDto
     * @return
     */
    Mono addAccount(AccountDto accountDto);


    /**
     * to fetch account details by health id number
     *
     * @param healthIdNumber
     * @return
     */
    Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber);


    /**
     * to update account details by health id number
     *
     * @param accountDto
     * @param healthIdNumber
     * @return
     */
    Mono updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber);

    /**
     * to fetch account details by xmluid
     *
     * @param xmluid
     * @return
     */
    Mono<AccountDto> getAccountByXmlUid(String xmluid);
    
    /**
     * to fetch accounts by list of healthId numbers
     *
     * @param xmluid
     * @return
     */
    Flux<AccountDto> getAccountsByHealthIdNumbers(List<String> healthIdNumbers);


}
