package in.gov.abdm.abha.enrollment.services.enrol.abha_address.impl;
import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.exception.database.constraint.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.AbhaAddressResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.AbhaAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AbhaAddressServiceImpl implements AbhaAddressService {

    public static final String ABHA_APP = "ABHA_APP";
    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    @Autowired
    HidPhrAddressService hidPhrAddressService;

    @Autowired
    AbhaDBClient abhaDBClient;

    private LinkedHashMap<String, String> errors;

    public static final String TXN_ID = "txnId";
    private String TxnId = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    @Override
    public Mono<SuggestAbhaResponseDto> getAbhaAddress(String txnId) {
        return transactionService.findTransactionDetailsFromDB(txnId)
                .flatMap(transactionDto -> {
                    if(transactionDto!=null)
                    {
                       return accountService.getAccountByHealthIdNumber(transactionDto.getHealthIdNumber())
                       .flatMap(accountDto ->
                        {
                            if(accountDto!=null)
                            {
                                Set<String> listAbhaSuggestion = populatePHRAddress(accountDto);
                                List<String> stringList = listAbhaSuggestion.stream().collect(Collectors.toList());

                                return hidPhrAddressService.findByPhrAddressIn(stringList)
                                .collectList().flatMap(Mono::just)
                                        .flatMap(hidPhrAddressDtoList -> {
                                            List<String> listAbhaAddressDb = hidPhrAddressDtoList.stream()
                                                    .map(HidPhrAddressDto::getPhrAddress)
                                                    .collect(Collectors.toList());

                                            listAbhaSuggestion.removeAll(listAbhaAddressDb);
                                            listAbhaSuggestion.removeIf(s -> s.length() >= 13 && s.length() <= 23);
                                            listAbhaSuggestion.stream().collect(Collectors.toList());
                                            return handleGetAbhaAddressResponse(transactionDto, listAbhaSuggestion);
                                        });
                            }
                            return Mono.empty();
                        }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                    }
                    return Mono.empty();
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<SuggestAbhaResponseDto> handleGetAbhaAddressResponse(TransactionDto transactionDto, Set<String> listAbhaSuggestion) {
        return Mono.just(SuggestAbhaResponseDto.builder()
                .txnId(String.valueOf(transactionDto.getTxnId()))
                .abhaAddressList(listAbhaSuggestion.stream().collect(Collectors.toList())).build());
    }

    private Set<String> populatePHRAddress(AccountDto accountDto) {
        Set<String> abhaAddress = new LinkedHashSet<String>();
        String dayOfBirth = !StringUtils.isEmpty(accountDto.getDayOfBirth()) ? accountDto.getDayOfBirth() : "";
        String monthOfBirth = !StringUtils.isEmpty(accountDto.getMonthOfBirth()) ? accountDto.getMonthOfBirth() : "";
        if (!StringUtils.isEmpty(accountDto.getFirstName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName()));
        }
        if (!StringUtils.isEmpty(accountDto.getLastName()) && !StringUtils.isEmpty(accountDto.getFirstName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName()));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), ".", accountDto.getLastName()));
        }
        if (!StringUtils.isEmpty(accountDto.getFirstName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName(), accountDto.getYearOfBirth()));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getYearOfBirth()));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), dayOfBirth, monthOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), dayOfBirth, monthOfBirth, accountDto.getYearOfBirth()));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName(), dayOfBirth, monthOfBirth, accountDto.getYearOfBirth()));
        }
        if (!StringUtils.isEmpty(accountDto.getLastName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getLastName(), dayOfBirth, monthOfBirth));
        }

        if (!StringUtils.isEmpty(accountDto.getEmail()) && accountDto.getEmail().contains("@")) {
            abhaAddress.add(populatePHRAddress(accountDto.getEmail().substring(0, accountDto.getEmail().indexOf('@'))));
        }
        return abhaAddress.stream().map(v -> v.replace(" ", "")).collect(Collectors.toSet());
    }

    public static String sanetizePhrAddress(String healthIdStr) {
        String phrIdSuffix = "";
        if (!StringUtils.isEmpty(healthIdStr)) {
            healthIdStr = healthIdStr.toLowerCase();
            if (!StringUtils.isEmpty(healthIdStr) && !healthIdStr.contains("@")) {
                healthIdStr = healthIdStr + phrIdSuffix;
            } else if (!StringUtils.isEmpty(healthIdStr) && healthIdStr.contains("@ndhm")) {
                healthIdStr = healthIdStr.replace("@ndhm", phrIdSuffix);
            }
            return healthIdStr.toLowerCase();
        }
        return healthIdStr;
    }

    public String populatePHRAddress(String... values) {
        return sanetizePhrAddress(Stream.of(values).map(data -> data.strip()).filter(data -> !StringUtils.isEmpty(data))
                .collect(Collectors.joining("")));
    }

    @Override
    public Mono<AbhaAddressResponseDto> createAbhaAddress(AbhaAddressRequestDto abhaAddressRequestDto) {
        return transactionService.findTransactionDetailsFromDB(abhaAddressRequestDto.getTxnId())
                .flatMap(transactionDto ->
                {
                    if(transactionDto!=null)
                    {
                        return accountService.getAccountByHealthIdNumber(transactionDto.getHealthIdNumber())
                        .flatMap(accountDto ->
                        {
                            if(accountDto!=null)
                            {
                               return hidPhrAddressService.getPhrAddressByPhrAddress(abhaAddressRequestDto.getPreferredAbhaAddress())
                                        .flatMap(hidPhrAddressDto ->
                                        {
                                            if(StringUtils.isEmpty(hidPhrAddressDto))
                                            {
                                                return updateHidAbhaAddress(accountDto,abhaAddressRequestDto,transactionDto);
                                            }
                                            else
                                            {
                                                throw new GenericExceptionMessage(AbhaConstants.ABHA_ADDRESS_ALREADY_EXISTS_EXCEPTION_MESSAGE);
                                            }
                                        }).switchIfEmpty(Mono.defer(()-> {
                                           return updateHidAbhaAddress(accountDto,abhaAddressRequestDto,transactionDto);
                                       }));
                            }
                            return Mono.empty();
                        }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                    }
                    return Mono.empty();
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AbhaAddressResponseDto> updateHidAbhaAddress(AccountDto accountDto,AbhaAddressRequestDto abhaAddressRequestDto,TransactionDto transactionDto)
    {
        if(abhaAddressRequestDto.getPreferred()==0)
        {
            Mono<HidPhrAddressDto> hidPhrAddressDtoMono
                    = hidPhrAddressService.createHidPhrAddressEntity(prepareHidPhrAddress(accountDto,abhaAddressRequestDto));
            return hidPhrAddressDtoMono.flatMap(hidPhrAddressDto ->
            {
               if(hidPhrAddressDto!=null)
               {
                   return handleCreateAbhaResponse(hidPhrAddressDto, transactionDto);
               }
                return Mono.empty();
            });
        }
        else if (abhaAddressRequestDto.getPreferred()==1)
        {
            Mono<HidPhrAddressDto> hidPhrAddressDtoMono
                    = hidPhrAddressService.findByByHealthIdNumber(accountDto.getHealthIdNumber());
            return hidPhrAddressDtoMono.flatMap(hidPhrAddressDto -> {
                if(hidPhrAddressDto!=null)
                {
                    hidPhrAddressDto.setPreferred(0);
                    Mono<HidPhrAddressDto> phrAddressDtoMono
                            = hidPhrAddressService.updateHidPhrAddressById(hidPhrAddressDto,hidPhrAddressDto.getHidPhrAddressId());
                    return phrAddressDtoMono.flatMap(hidPhrAddressDto1 -> {
                        if(hidPhrAddressDto1!=null)
                        {
                            Mono<HidPhrAddressDto> hidPhrAddressDtoMono1
                                    = hidPhrAddressService.createHidPhrAddressEntity(prepareHidPhrAddress(accountDto,abhaAddressRequestDto));
                            return hidPhrAddressDtoMono1.flatMap(hidPhrAddressDto2 -> {
                                return handleCreateAbhaResponse(hidPhrAddressDto2,transactionDto);
                            });
                        }
                        return Mono.empty();
                    });
                }
                return Mono.empty();
            });
        }
        return Mono.empty();
    }

    private Mono<AbhaAddressResponseDto> handleCreateAbhaResponse(HidPhrAddressDto hidPhrAddressDto, TransactionDto transactionDto) {
        return Mono.just(AbhaAddressResponseDto.builder()
                .txnId(String.valueOf(transactionDto.getTxnId()))
                .healthIdNumber(hidPhrAddressDto.getHealthIdNumber())
                .preferredAbhaAddress(hidPhrAddressDto.getPhrAddress())
                .build());
    }

    private HidPhrAddressDto prepareHidPhrAddress(AccountDto accountDto,AbhaAddressRequestDto abhaAddressRequestDto) {
        return HidPhrAddressDto.builder()
                .healthIdNumber(accountDto.getHealthIdNumber())
                .phrAddress(abhaAddressRequestDto.getPreferredAbhaAddress())
                .status("ACTIVE")
                .preferred(abhaAddressRequestDto.getPreferred())
                .lastModifiedBy(ABHA_APP)
                .lastModifiedDate(LocalDateTime.now())
                .hasMigrated("N")
                .createdBy(ABHA_APP)
                .createdDate(LocalDateTime.now())
                .linked(1)
                .cmMigrated(0)
                .isNewHidPhrAddress(true)
                .build();
    }


    @Override
    public void validateRequest(String txnId) {
        errors = new LinkedHashMap<>();
        if (!isValidTxnId(txnId)) {
            errors.put(TXN_ID, AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD);
        }
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isValidTxnId(String txnId) {
        if (Pattern.compile(TxnId).matcher(txnId).matches()) {
            return true;
        } else {
            return false;
        }
    }
}