package in.gov.abdm.abha.enrollment.services.database.account.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    public static final String PARSER_EXCEPTION_OCCURRED_DURING_PARSING = "Parser Exception occurred during parsing :";
    public static final String EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB = "Exception in parsing Invalid value of DOB: {}";
    private DateFormat KYC_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    private WebClient webClient;

    @Autowired
    AbhaDBClient abhaDBClient;

    @Override
    public Mono<AccountDto> findByXmlUid(String xmlUid) {
        return abhaDBClient.getEntityById(AccountDto.class, xmlUid);
    }

    @Override
    public AccountDto prepareNewAccount(TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        // AccountEntity searchedUserByMobile = null;
        AccountDto newUser = new AccountDto();
        // UserDto user = new UserDto();
        //DistrictDTO districtDTO = null;


//        if (!transactionDto.isKycVerified()) {
//            throw new UidaiException("KYC_NOT_DONE");
//        }
        // TODO check user account against same mobile no. if account exist more than 10 with
        // same number
        //
        //userDetailsComponent.validateMaxAccountsByMobile(transactionDto.getMobile());
        //TODO(check) no need to check KYC verified again
//        if (StringUtils.isEmpty(transactionDto.getHealthIdNumber())) {
//            if (!StringUtils.isEmpty(transactionDto.getKycType()) && !KycAuthType.FINGERSCAN.name().equals(transactionDto.getKycType()) && !transactionDto.isMobileVerified()) {
//                throw new MobileNotVerifiedException();
//            }
//        }
        //UserKycData kycData = transactionEnitityToUserKycData.apply(transactionDto);
        //TODO user should not create multiple accounts
        //TODO search user by xmlUID
//        UserEntity searchedUser = userRepository.findByXmlUID(transactionDto.getXmlUID());
        //TODO search user by some demographic details
//        if (!StringUtils.isEmpty(transactionDto.getMobile())) {
//            searchedUserByMobile = fuzzySearchService.findByFuzzySearch(transactionDto.getMobile(), transactionDto.getName(), transactionDto.getGender(), transactionDto.getYearOfBirth());
//
//            // to segregate hid creation using other id document
//            if (!Objects.isNull(searchedUserByMobile) && StringUtils.hasLength(searchedUserByMobile.getVerificationStatus())) {
//                searchedUserByMobile = null;
//            }
//
//        }
        //TODO handle existing users
//        boolean isNewAccount = true;
//
//        if (accountEntity != null) {
//            newUser = accountEntity;
//            log.warn("found User by Aadhaar search while creating account: " + accountEntity.getHealthIdNumber());
//            if (!StringUtils.isBlank(transactionDto.getMobile())) {
//                accountEntity.setMobile(transactionDto.getMobile());
//                accountEntity.getAccountAuthMethods().add(AccountAuthMethods.MOBILE_OTP);
//                accountEntity = updateReKYC(searchedUser, transactionDto, kycData);
//                accountEntity = userService.save(searchedUser);
//            }
//            user.setNew(false);
//            isNewAccount = false;
//        } else if (searchedUserByMobile != null) {
//            if (transactionDto.isKycVerified() && searchedUserByMobile.getXmlUID() != null && !searchedUserByMobile.getXmlUID().equals(transactionDto.getXmlUID())) {
//                isNewAccount = true;
//            } else {
//                newUser = searchedUserByMobile;
//                log.warn("found User by Mobile search while creating account by Aadhaar: " + searchedUserByMobile.getHealthIdNumber());
//
//                searchedUserByMobile = updateReKYC(searchedUserByMobile, transactionDto, kycData);
//                userService.save(searchedUserByMobile);
//
//                user.setNew(false);
//                isNewAccount = false;
//            }
//        }

        AccountDto accountDto = findUserAlreadyExist(transactionDto);
        if (isItNewUser(accountDto)) {
            newUser.setAddress(transactionDto.getLoc());
            newUser.setName(transactionDto.getName());
            newUser.setGender(transactionDto.getGender());
            // Stor storing kycPhoto.

            //TODO update kyc photo in user entity
            // newUser.setKycPhoto(transactionDto.getKycPhoto());
            if (!StringUtils.isBlank(transactionDto.getPincode())) {
                newUser.setPincode(transactionDto.getPincode());
            }
            newUser.setKycDob(transactionDto.getKycdob());
            setDateOfBrith(transactionDto.getKycdob(), newUser);
            newUser.setDistrictName(transactionDto.getDistrictName());
            newUser.setStateName(transactionDto.getStateName());
            //TODO LGD service implementation
            //StatesDTO state = lgdService.getStateByCodeOrName(transactionDto.getStateName());
//            if (!Objects.isNull(state)) {
//                newUser.setStateCode(state.getCode());
//                newUser.setStateName(state.getName());
//                districtDTO = lgdService.getDistrictByCodeOrName(state.getCode(), transactionDto.getDistrictName());
//                newUser.setDistrictCode(Objects.nonNull(districtDTO) ? districtDTO.getCode() : "");
//            } else {
//                log.warn("State %s not found in LGD service.", transactionDto.getStateName());
//            }
            newUser.setSubDistrictName(transactionDto.getSubDistrictName());
            newUser.setTownName(transactionDto.getTownName());
            newUser.setXmlUID(transactionDto.getXmluid());
            //TODO email settings
//            if (!StringUtils.isBlank(accountRequest.getEmail())) {
//                newUser.setEmail(accountRequest.getEmail());
//            }
            if (!StringUtils.isBlank(transactionDto.getEmail())) {
                newUser.setEmail(transactionDto.getEmail());
            }
            // TODO Note :- this variable consume email address instead of boolean need to
            // change in future
//            newUser.setEmail_verified(transactionDto.isEmailVerified() ? transactionDto.getEmail() : null);

            //TODO user inputs on user details
//            if (!StringUtils.isBlank(accountRequest.getFirstName())) {
//                newUser.setFirstName(accountRequest.getFirstName());
//            }
//            if (!isBlank(accountRequest.getLastName())) {
//                newUser.setLastName(accountRequest.getLastName());
//            }
//            if (!isBlank(accountRequest.getMiddleName())) {
//                newUser.setMiddleName(accountRequest.getMiddleName());
//            }
//            if (!isBlank(accountRequest.getPassword())) {
//                newUser.setPassword(bcryptEncoder.encode(accountRequest.getPassword()));
//            }
//            if (!isBlank(accountRequest.getProfilePhoto())) {
//                newUser.setProfilePhoto(accountRequest.getProfilePhoto().getBytes());
//                byte[] compressedProfilePhoto = transactionEnitityToUserKycData.checkImageValidationAndCompressImage(newUser, accountRequest.getProfilePhoto(), true);
//                compressedProfilePhoto = Objects.nonNull(compressedProfilePhoto) ? compressedProfilePhoto : newUser.getProfilePhoto();
//                newUser.setProfilePhoto(compressedProfilePhoto);
//                newUser.setProfilePhotoCompressed(true);
//            }

            newUser.setConsentVersion(enrolByAadhaarRequestDto.getConsent().getVersion());

            Set<AccountAuthMethods> accountAuthMethods = new HashSet<>();
            accountAuthMethods.add(AccountAuthMethods.AADHAAR_OTP);
            accountAuthMethods.add(AccountAuthMethods.AADHAAR_BIO);
            accountAuthMethods.add(AccountAuthMethods.DEMOGRAPHICS);
            if (!StringUtils.isBlank(newUser.getPassword())) {
                accountAuthMethods.add(AccountAuthMethods.PASSWORD);
            }
            if (transactionDto.isMobileVerified() && !StringUtils.isBlank(transactionDto.getMobile())) {
                newUser.setMobile(transactionDto.getMobile());
                accountAuthMethods.add(AccountAuthMethods.MOBILE_OTP);
            }
            //TODO check what to set here
            //newUser.setKycData(kycData);
            //TODO set account Auth method
            //newUser.setAccountAuthMethods(accountAuthMethods);
            newUser.setKycVerified(true);
            newUser.setStatus(AccountStatus.ACTIVE.toString());
            breakName(newUser);
            //user.setNew(true);
            //TODO create phr address
//            String healthId = null;
//            if (!isBlank(transactionDto.getPhrAddress())) {
//                healthId = transactionDto.getPhrAddress().toLowerCase();
//                newUser = phrAddressComponent.addPhrAddress(newUser, healthId);
//            } else if (!isBlank(accountRequest.getHealthId())) {
//                healthId = accountRequest.getHealthId().toLowerCase();
//            }
//            if (!StringUtils.isEmpty(healthId)) {
//                newUser = phrAddressComponent.addPhrAddress(newUser, healthId);
//            }

            //userService.save(newUser);
        }
//        fetchDefaultPhrAddress(newUser.getHealthIdNumber());
//        BeanUtils.copyProperties(newUser, user);
//        if (!ObjectUtils.isEmpty(newUser.getKycPhoto())) {
//            user.setKycPhoto(new String(newUser.getKycPhoto()));
//        }
//        if (newUser.getProfilePhoto() != null) {
//            user.setProfilePhoto(new String(newUser.getProfilePhoto()));
//        }
//        user.setHealthId(phrAddressService.fetchPhrAdress(newUser.getHealthIdNumber()));
        //TODO notify user with sms
        //smsService.sendHealthIdSuccessNotification(user);
        //TODO delete transaction
        //authTransactionService.delete(transactionDto);
        // return user;

        newUser.setCreatedDate(LocalDateTime.now());


        return newUser;
    }

    @Override
    public boolean isItNewUser(AccountDto accountDto) {
        //todo check existing user
        return accountDto == null || accountDto.getName() == null;
    }

    @Override
    public Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber) {
        return abhaDBClient.getAccountEntityById(AccountDto.class,healthIdNumber);
    }

    @Override
    public Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber) {
        return abhaDBClient.updateEntity(AccountDto.class,accountDto,healthIdNumber);
    }

    @Override
    public Flux<AccountDto> getAccountsByHealthIdNumbers(List<String> healthIdNumbers) {
        return abhaDBClient.getFluxEntity(AccountDto.class,healthIdNumbers);
    }

    private void breakName(AccountDto accountDto) {

        String firstName = "";
        String lastName = "";
        String middleName = "";

        if (!StringUtils.isEmpty(accountDto.getName())) {
            List<String> name = new ArrayList<>(Arrays.asList(accountDto.getName().split(" ")));
            if (name.size() == 1) {
                firstName = name.get(0);
            } else if (name.size() == 2) {
                firstName = name.get(0);
                lastName = name.get(1);
            } else {
                firstName = name.get(0);
                lastName = name.get(name.size() - 1);
                name.remove(0);
                name.remove(name.size() - 1);
                middleName = String.join(" ", name);
            }

        }
        accountDto.setFirstName(GeneralUtils.stringTrimmer(firstName));
        accountDto.setLastName(GeneralUtils.stringTrimmer(lastName));
        accountDto.setMiddleName(GeneralUtils.stringTrimmer(middleName));
    }

    private void setDateOfBrith(String birthdate, AccountDto accountDto) {
        if (birthdate != null && birthdate.length() > 4) {
            try {

                LocalDate birthDate = KYC_DATE_FORMAT.parse(birthdate).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                accountDto.setMonthOfBirth(String.valueOf(birthDate.getMonth().getValue()));
                accountDto.setDayOfBirth(String.valueOf(birthDate.getDayOfMonth()));
                accountDto.setYearOfBirth(String.valueOf(birthDate.getYear()));
            } catch (ParseException e) {
                log.error(PARSER_EXCEPTION_OCCURRED_DURING_PARSING, e);
            } catch (Exception ex) {
                log.error(EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB, birthdate);
            }
        } else if (birthdate != null && birthdate.length() == 4) {
            accountDto.setYearOfBirth(birthdate);
        }
    }

    private AccountDto findUserAlreadyExist(TransactionDto transactionDto) {
        //TODO find user using xmluid or demographic details
        return new AccountDto();
    }

    public Mono<AccountDto> createAccountEntity(AccountDto accountDto) {
        //TODO Call to DB service to save entity

        accountDto.setNewAccount(true);

        return abhaDBClient.addEntity(AccountDto.class, accountDto);
    }
}
