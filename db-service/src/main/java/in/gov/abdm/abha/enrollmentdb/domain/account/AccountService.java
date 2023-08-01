package in.gov.abdm.abha.enrollmentdb.domain.account;

import java.util.List;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.de_duplication.DeDuplicationRequest;
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
    Mono<AccountDto> addAccount(AccountDto accountDto);


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
    Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber);

    /**
     * to fetch account details by xmluid
     *
     * @param xmluid
     * @return
     */
    Mono<AccountDto> getAccountByXmlUid(String xmluid);

    /**
     *
     * @param healthIdNumbers
     * @return
     */
    Flux<AccountDto> getAccountsByHealthIdNumbers(List<String> healthIdNumbers);

    Mono<AccountDto> getAccountByDocumentCode(String documentCode);

    Mono<Integer> getMobileLinkedAccountsCount(String mobileNumber);
    Mono<Integer> getEmailLinkedAccountsCount(String email);
    Mono<AccountDto> checkDeDuplication(DeDuplicationRequest request);

}
