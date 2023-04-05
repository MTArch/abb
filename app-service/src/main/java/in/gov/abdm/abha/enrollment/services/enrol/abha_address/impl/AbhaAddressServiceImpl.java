package in.gov.abdm.abha.enrollment.services.enrol.abha_address.impl;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.exception.application.AbhaConflictException;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.AbhaAddressResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.AbhaAddressService;
import in.gov.abdm.error.ABDMError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    @Value("${enrollment.domain}")
    private String abhaAddressExtension;

    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    @Autowired
    HidPhrAddressService hidPhrAddressService;

    public static final String TXN_ID = "txnId";
    private static final String TXN_ID_PATTERN = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

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
                                                    stringList.removeAll(listAbhaAddressDb);
                                                    List<String> list1 = stringList.stream().collect(Collectors.toList());
                                                    List<String> list2= list1.stream()
                                                            .map(s -> s.replace(StringConstants.AT + abhaAddressExtension,""))
                                                            .collect(Collectors.toList());
                                                    list2.removeIf(s -> s.length() < 8 || s.length()>18);
                                                    return handleGetAbhaAddressResponse(transactionDto, list2);
                                                });
                                    }
                                    return Mono.empty();
                                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                    }
                    return Mono.empty();
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<SuggestAbhaResponseDto> handleGetAbhaAddressResponse(TransactionDto transactionDto, List<String> listAbhaSuggestion) {
        return Mono.just(SuggestAbhaResponseDto.builder()
                .txnId(String.valueOf(transactionDto.getTxnId()))
                .abhaAddressList(listAbhaSuggestion).build());
    }

    private Set<String> populatePHRAddress(AccountDto accountDto) {
        Set<String> abhaAddress = new LinkedHashSet<>();
        String dayOfBirth = !StringUtils.isEmpty(accountDto.getDayOfBirth()) ? accountDto.getDayOfBirth() : "";
        String monthOfBirth = !StringUtils.isEmpty(accountDto.getMonthOfBirth()) ? accountDto.getMonthOfBirth() : "";
        String yearOfBirth = !StringUtils.isEmpty(accountDto.getYearOfBirth()) ? accountDto.getYearOfBirth() : "";
        if (!StringUtils.isEmpty(accountDto.getFirstName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName()));
        }
        if (!StringUtils.isEmpty(accountDto.getLastName()) && !StringUtils.isEmpty(accountDto.getFirstName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName()));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), ".", accountDto.getLastName()));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), "_", accountDto.getLastName()));
        }
        if (!StringUtils.isEmpty(accountDto.getFirstName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName(), yearOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName(),".", yearOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName(),"_", yearOfBirth));

            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), yearOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),".", yearOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),"_", yearOfBirth));

            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), dayOfBirth, monthOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),".", dayOfBirth, monthOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),"_", dayOfBirth, monthOfBirth));

            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), dayOfBirth, monthOfBirth, yearOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),".", dayOfBirth, monthOfBirth, yearOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),"_", dayOfBirth, monthOfBirth, yearOfBirth));

            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName(), dayOfBirth, monthOfBirth, yearOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName(),".", dayOfBirth, monthOfBirth, yearOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(), accountDto.getLastName(),"_", dayOfBirth, monthOfBirth, yearOfBirth));
        }
        if (!StringUtils.isEmpty(accountDto.getLastName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getLastName(), dayOfBirth, monthOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getLastName(),".", dayOfBirth, monthOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getLastName(),"_", dayOfBirth, monthOfBirth));
        }

        if (!StringUtils.isEmpty(accountDto.getLastName()) && !StringUtils.isEmpty(accountDto.getFirstName())) {
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),accountDto.getLastName(),dayOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),accountDto.getLastName(),".",dayOfBirth));
            abhaAddress.add(populatePHRAddress(accountDto.getFirstName(),accountDto.getLastName(),"_",dayOfBirth));
        }

        if (!StringUtils.isEmpty(accountDto.getLastName()) && !StringUtils.isEmpty(accountDto.getFirstName())) {
            abhaAddress.add(populatePHRAddress(dayOfBirth,monthOfBirth,"_",accountDto.getFirstName(),".",accountDto.getLastName()));
            abhaAddress.add(populatePHRAddress(yearOfBirth,"_",accountDto.getFirstName(),".",accountDto.getLastName()));
            abhaAddress.add(populatePHRAddress(dayOfBirth,"_",accountDto.getFirstName(),".",accountDto.getLastName()));
        }

        if (!StringUtils.isEmpty(accountDto.getEmail()) && accountDto.getEmail().contains("@")) {
            abhaAddress.add(populatePHRAddress(accountDto.getEmail().substring(0, accountDto.getEmail().indexOf('@'))));
        }
        return abhaAddress.stream().map(v -> v.replace(" ", "")).collect(Collectors.toSet());
    }

    public String sanetizePhrAddress(String healthIdStr) {
        String phrIdSuffix = "";
        if (!StringUtils.isEmpty(healthIdStr)) {
            healthIdStr = healthIdStr.toLowerCase();
            if (!StringUtils.isEmpty(healthIdStr) && !healthIdStr.contains("@")) {
                healthIdStr = healthIdStr + phrIdSuffix;
            } else if (!StringUtils.isEmpty(healthIdStr) && healthIdStr.contains("@ndhm")) {
                healthIdStr = healthIdStr.replace("@ndhm", phrIdSuffix);
            }
            return healthIdStr.toLowerCase()+StringConstants.AT+ abhaAddressExtension;
        }
        return healthIdStr;
    }

    public String populatePHRAddress(String... values) {
        return sanetizePhrAddress(Stream.of(values).map(String::strip).filter(data -> !StringUtils.isEmpty(data))
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
                                        return hidPhrAddressService.getPhrAddressByPhrAddress(abhaAddressRequestDto.getPreferredAbhaAddress().toLowerCase()+StringConstants.AT + abhaAddressExtension)
                                                .flatMap(hidPhrAddressDto ->
                                                {
                                                    if(StringUtils.isEmpty(hidPhrAddressDto.getHealthIdNumber()))
                                                    {
                                                        return updateHidAbhaAddress(accountDto,abhaAddressRequestDto,transactionDto);
                                                    }
                                                    else
                                                    {
                                                        throw new AbhaConflictException(ABDMError.ABHA_ADDRESS_EXIST.getCode(), ABDMError.ABHA_ADDRESS_EXIST.getMessage());
                                                    }
                                                }).switchIfEmpty(Mono.defer(()-> updateHidAbhaAddress(accountDto,abhaAddressRequestDto,transactionDto)));
                                    }
                                    return Mono.empty();
                                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
                    }
                    return Mono.empty();
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AbhaAddressResponseDto> updateHidAbhaAddress(AccountDto accountDto,AbhaAddressRequestDto abhaAddressRequestDto,TransactionDto transactionDto)
    {
        if (abhaAddressRequestDto.getPreferred()!=null && abhaAddressRequestDto.getPreferred()==1)
        {
            Mono<HidPhrAddressDto> hidPhrAddressDtoMono
                    = hidPhrAddressService.findByHealthIdNumber(accountDto.getHealthIdNumber());
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
                            return hidPhrAddressDtoMono1.flatMap(hidPhrAddressDto2 -> handleCreateAbhaResponse(hidPhrAddressDto2,transactionDto));
                        }
                        return Mono.empty();
                    });
                }
                return Mono.empty();
            }).switchIfEmpty(Mono.defer(()-> {

                Mono<HidPhrAddressDto> hidPhrAddressDtoMono1
                        = hidPhrAddressService.createHidPhrAddressEntity(prepareHidPhrAddress(accountDto,abhaAddressRequestDto));
                return hidPhrAddressDtoMono1.flatMap(hidPhrAddressDto2 -> handleCreateAbhaResponse(hidPhrAddressDto2,transactionDto));
            }));
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
                .phrAddress(abhaAddressRequestDto.getPreferredAbhaAddress()+StringConstants.AT+ abhaAddressExtension)
                .status(AccountStatus.ACTIVE.getValue())
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
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();
        if (!isValidTxnId(txnId)) {
            errors.put(TXN_ID, AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD);
        }
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isValidTxnId(String txnId) {
        return Pattern.compile(TXN_ID_PATTERN).matcher(txnId).matches();
    }
}