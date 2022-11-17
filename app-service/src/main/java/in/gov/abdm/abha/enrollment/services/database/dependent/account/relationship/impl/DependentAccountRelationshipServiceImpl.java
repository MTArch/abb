package in.gov.abdm.abha.enrollment.services.database.dependent.account.relationship.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.services.database.dependent.account.relationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class DependentAccountRelationshipServiceImpl implements DependentAccountRelationshipService {

    public static final String PARSER_EXCEPTION_OCCURRED_DURING_PARSING = "Parser Exception occurred during parsing :";
    public static final String EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB = "Exception in parsing Invalid value of DOB: {}";
    private DateFormat KYC_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    @Autowired
    AbhaDBClient abhaDBClient;

//    @Override
//    public DependentAccountRelationshipDto prepareDependentAccount(LinkParentRequestDto linkParentRequestDto, AccountDto accountDto) {
//        return DependentAccountRelationshipDto.builder()
//                .parentHealthIdNumber(linkParentRequestDto.getParentAbhaRequestDto().getABHANumber())
//                .dependentHealthIdNumber(accountDto.getHealthIdNumber())
//                .relatedAs(linkParentRequestDto.getParentAbhaRequestDto().getRelationship())
//                .relationshipProofDocumentLocation(linkParentRequestDto.getParentAbhaRequestDto().getDocument())
//                .createdBy("Anchal")
//                .updatedBy("Anchal")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .build();
//    }

    @Override
    public List<DependentAccountRelationshipDto> prepareDependentAccount(LinkParentRequestDto linkParentRequestDto, AccountDto accountDto) {
        List<DependentAccountRelationshipDto> list = new ArrayList<>();
        DependentAccountRelationshipDto dependentAccountDto = new DependentAccountRelationshipDto();
        if(linkParentRequestDto.getParentAbhaRequestDtoList().size()>0 && accountDto!=null) {
            for (int i = 0; i < linkParentRequestDto.getParentAbhaRequestDtoList().size(); i++) {
                dependentAccountDto.setParentHealthIdNumber(linkParentRequestDto.getParentAbhaRequestDtoList().get(i).getABHANumber());
                dependentAccountDto.setDependentHealthIdNumber(accountDto.getHealthIdNumber());
                dependentAccountDto.setRelatedAs(linkParentRequestDto.getParentAbhaRequestDtoList().get(i).getRelationship());
                dependentAccountDto.setRelationshipProofDocumentLocation(linkParentRequestDto.getParentAbhaRequestDtoList().get(i).getDocument());
                dependentAccountDto.setCreatedBy("anchal");
                dependentAccountDto.setUpdatedBy("anchal");
                dependentAccountDto.setCreatedDate(LocalDateTime.now());
                dependentAccountDto.setUpdatedDate(LocalDateTime.now());
                list.add(dependentAccountDto);
            }
        }
        return list;
    }

//    @Override
//    public Mono<DependentAccountRelationshipDto> createDependentAccountEntity(DependentAccountRelationshipDto dependentAccountRelationshipDto) {
//        return abhaEnrollmentDBClient.addEntity(DependentAccountRelationshipDto.class, dependentAccountRelationshipDto);
//    }


//    @Override
    public Mono<DependentAccountRelationshipDto> createDependentAccountEntity(List<DependentAccountRelationshipDto> dependentAccountRelationshipList) {
        return abhaDBClient.addFluxEntity(DependentAccountRelationshipDto.class, dependentAccountRelationshipList);
    }

//    @Override
    public AccountDto prepareUpdateAccount(TransactionDto transactionDto, LinkParentRequestDto linkParentRequestDto) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAddress(transactionDto.getLoc());
        accountDto.setName(transactionDto.getName());
        accountDto.setGender(transactionDto.getGender());

        //TODO update kyc photo in user entity
        // accountDto.setKycPhoto(transactionDto.getKycPhoto());
        if (!StringUtils.isBlank(transactionDto.getPincode())) {
            accountDto.setPincode(transactionDto.getPincode());
        }
        accountDto.setKycDob(transactionDto.getKycdob());
        setDateOfBrith(transactionDto.getKycdob(), accountDto);
        accountDto.setDistrictName(transactionDto.getDistrictName());
        accountDto.setStateName(transactionDto.getStateName());

        accountDto.setSubDistrictName(transactionDto.getSubDistrictName());
        accountDto.setTownName(transactionDto.getTownName());
        accountDto.setXmlUID(transactionDto.getXmluid());

        if (!StringUtils.isBlank(transactionDto.getEmail())) {
            accountDto.setEmail(transactionDto.getEmail());
        }
        accountDto.setConsentVersion(linkParentRequestDto.getConsentDto().getVersion());

        Set<AccountAuthMethods> accountAuthMethods = new HashSet<>();
        accountAuthMethods.add(AccountAuthMethods.AADHAAR_OTP);
        accountAuthMethods.add(AccountAuthMethods.AADHAAR_BIO);
        accountAuthMethods.add(AccountAuthMethods.DEMOGRAPHICS);
        if (!StringUtils.isBlank(accountDto.getPassword())) {
            accountAuthMethods.add(AccountAuthMethods.PASSWORD);
        }
        if (transactionDto.isMobileVerified() && !StringUtils.isBlank(transactionDto.getMobile())) {
            accountDto.setMobile(transactionDto.getMobile());
            accountAuthMethods.add(AccountAuthMethods.MOBILE_OTP);
        }
        //accountDto.setAccountAuthMethods(accountAuthMethods);
        accountDto.setKycVerified(true);
        accountDto.setStatus(AccountStatus.ACTIVE.toString());
        breakName(accountDto);
        accountDto.setCreatedDate(LocalDateTime.now());
        return accountDto;
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
}
