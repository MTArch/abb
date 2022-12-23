package in.gov.abdm.abha.enrollment.services.enrol.driving_licence;

import in.gov.abdm.abha.enrollment.client.DocumentClient;
import in.gov.abdm.abha.enrollment.client.LGDClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.exception.database.constraint.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLRequest;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EnrolUsingDrivingLicence {

    private static final String MOBILE_NUMBER_VERIFICATION_IS_PENDING = "Mobile number verification is pending.";
    private static final String FAILED_TO_DELETE_TRANSACTION = "Failed to delete Transaction: ";
    private static final String TRANSACTION = "Transaction ";
    private static final String FOUND = " found.";
    private static final String MOBILE_NUMBER_IS_VERIFIED = "Mobile number is verified";
    private static final String CLOSING = ")";
    private static final String WITH_SAME_DL = ") with same DL (";
    private static final String FOUND_ACCOUNT = "Found account (";
    private static final String ACCOUNT_NOT_FOUND_WITH_DL_VERIFYING_DL_DETAILS = "Account not found with DL, verifying DL details";
    private static final String MOBILE_NUMBER_NOT_VERIFIED = "mobile number not verified";
    private static final String DL_DETAILS_VERIFIED_CREATING_NEW_ENROLLMENT_ACCOUNT = "DL details verified, creating new Enrollment Account";
    private static final String DL_DETAILS_NOT_VERIFIED = "DL details not verified";
    private static final String NEW_ENROLLMENT_ACCOUNT_CREATED_AND_UPDATED_IN_DB = "new enrollment account created and updated in DB";
    private static final String TRANSACTION_DELETED = "transaction deleted";
    private static final String DL_DOCUMENTS_STORED_IN_ADV_DB = "DL documents stored in ADV DB";

    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    @Autowired
    DocumentClient documentClient;

    @Autowired
    LGDClient lgdClient;

    public Mono<EnrolByDocumentResponseDto> verifyAndCreateAccount(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return transactionService.findTransactionDetailsFromDB(enrolByDocumentRequestDto.getTxnId())
                .flatMap(txnDto -> {
                    log.info(TRANSACTION + txnDto.getTxnId() + FOUND);
                    if (txnDto.isMobileVerified()) {
                        log.info(MOBILE_NUMBER_IS_VERIFIED);
                        return accountService.getAccountByDocumentCode(GeneralUtils.documentChecksum(enrolByDocumentRequestDto.getDocumentId()))
                                .flatMap(accountDto -> {
                                    log.info(FOUND_ACCOUNT + accountDto.getHealthIdNumber() + WITH_SAME_DL + enrolByDocumentRequestDto.getDocumentId() + CLOSING);
                                    return prepareErolByDLResponse(accountDto);
                                }).switchIfEmpty(Mono.defer(() -> {
                                    //verify DL and create new account
                                    log.info(ACCOUNT_NOT_FOUND_WITH_DL_VERIFYING_DL_DETAILS);
                                    return verifyDrivingLicence(enrolByDocumentRequestDto, txnDto);
                                }));
                    } else {
                        log.info(MOBILE_NUMBER_NOT_VERIFIED);
                        throw new GenericExceptionMessage(MOBILE_NUMBER_VERIFICATION_IS_PENDING);
                    }
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<EnrolByDocumentResponseDto> verifyDrivingLicence(EnrolByDocumentRequestDto enrolByDocumentRequestDto, TransactionDto txnDto) {
        return documentClient.verify(VerifyDLRequest.builder()
                .documentType(AbhaConstants.DRIVING_LICENCE)
                .documentId(enrolByDocumentRequestDto.getDocumentId())
                .dob(enrolByDocumentRequestDto.getDob())
                .build()).flatMap(verifyDLResponse -> {
            if (verifyDLResponse.getAuthResult().equals(StringConstants.SUCCESS)) {
                //create new account
                log.info(DL_DETAILS_VERIFIED_CREATING_NEW_ENROLLMENT_ACCOUNT);
                return createDLAccount(enrolByDocumentRequestDto, txnDto);
            } else {
                //failure response
                log.info(DL_DETAILS_NOT_VERIFIED);
                throw new GenericExceptionMessage(verifyDLResponse.getMessage());
            }
        });
    }

    private Mono<EnrolByDocumentResponseDto> createDLAccount(EnrolByDocumentRequestDto enrolByDocumentRequestDto, TransactionDto transactionDto) {
        String enrollmentNumber = AbhaNumberGenerator.generateAbhaNumber();
        AccountDto accountDto = AccountDto.builder()
                .healthIdNumber(enrollmentNumber)
                .verificationStatus(AbhaConstants.PROVISIONAL)
                .firstName(enrolByDocumentRequestDto.getFirstName())
                .middleName(enrolByDocumentRequestDto.getMiddleName())
                .lastName(enrolByDocumentRequestDto.getLastName())
                .dayOfBirth(enrolByDocumentRequestDto.getDob())
                .gender(enrolByDocumentRequestDto.getGender())
                .mobile(transactionDto.getMobile())
                .address(enrolByDocumentRequestDto.getAddress())
                .districtName(enrolByDocumentRequestDto.getDistrict())
                .stateName(enrolByDocumentRequestDto.getState())
                .type(AbhaType.STANDARD)
                .pincode(enrolByDocumentRequestDto.getPinCode())
                .kycVerified(false)
                .status(AccountStatus.IN_ACTIVE.getValue())
                .documentCode(GeneralUtils.documentChecksum(enrolByDocumentRequestDto.getDocumentId()))
                .healthId(AbhaAddressGenerator.generateDefaultAbhaAddress(enrollmentNumber))
                .build();

        return lgdClient.getLgdDistrictDetails(enrolByDocumentRequestDto.getPinCode())
                .flatMap(lgdDistrictResponses -> {
                    LgdDistrictResponse lgdDistrictResponse = Common.getLGDDetails(lgdDistrictResponses);
                    accountDto.setStateCode(lgdDistrictResponse.getStateCode());
                    accountDto.setDistrictCode(lgdDistrictResponse.getDistrictCode());
                    return accountService.createAccountEntity(accountDto)
                            .flatMap(accountDtoResponse -> {
                                log.info(NEW_ENROLLMENT_ACCOUNT_CREATED_AND_UPDATED_IN_DB);
                                return transactionService.deleteTransactionEntity(transactionDto.getTxnId().toString())
                                        .flatMap(responseEntity -> {
                                            if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                                                log.warn(FAILED_TO_DELETE_TRANSACTION + transactionDto.getTxnId().toString());
                                            } else {
                                                log.info(TRANSACTION_DELETED);
                                            }
                                            return addDocumentsInIdentityDocumentEntity(accountDtoResponse, enrolByDocumentRequestDto);
                                        });
                            });
                });
    }

    private Mono<EnrolByDocumentResponseDto> addDocumentsInIdentityDocumentEntity(AccountDto accountDto, EnrolByDocumentRequestDto enrolByDocumentRequestDto) {

        IdentityDocumentsDto identityDocumentsDto = new IdentityDocumentsDto();
        identityDocumentsDto.setDocumentNumber(accountDto.getDocumentCode());
        identityDocumentsDto.setDocumentType(AbhaConstants.DRIVING_LICENCE);
        identityDocumentsDto.setDob(accountDto.getDayOfBirth());
        identityDocumentsDto.setGender(accountDto.getGender());
        identityDocumentsDto.setFirstName(accountDto.getFirstName());
        identityDocumentsDto.setLastName(accountDto.getLastName());
        identityDocumentsDto.setMiddleName(accountDto.getMiddleName());
        identityDocumentsDto.setStatus(accountDto.getStatus());
        identityDocumentsDto.setHealthIdNumber(accountDto.getHealthIdNumber());
        identityDocumentsDto.setPhoto(enrolByDocumentRequestDto.getFrontSidePhoto());
        identityDocumentsDto.setPhotoBack(enrolByDocumentRequestDto.getBackSidePhoto());

        return documentClient.addIdentityDocuments(identityDocumentsDto)
                .flatMap(identityDocumentsDtoResponse -> {
                    log.info(DL_DOCUMENTS_STORED_IN_ADV_DB);
                    return prepareErolByDLResponse(accountDto);
                });
    }

    private Mono<EnrolByDocumentResponseDto> prepareErolByDLResponse(AccountDto accountDto) {
        EnrolProfileDto enrolProfileDto = EnrolProfileDto.builder()
                .enrolmentNumber(accountDto.getHealthIdNumber())
                .enrolmentState(accountDto.getVerificationStatus())
                .firstName(accountDto.getFirstName())
                .middleName(accountDto.getMiddleName())
                .lastName(accountDto.getLastName())
                .dob(accountDto.getDayOfBirth())
                .gender(accountDto.getGender())
                .mobile(accountDto.getMobile())
                .email(accountDto.getEmail())
                .emailVerified(accountDto.getEmailVerified())
                .address(accountDto.getAddress())
                .districtCode(accountDto.getDistrictCode())
                .stateCode(accountDto.getStateCode())
                .abhaType(accountDto.getType().getValue())
                .pinCode(accountDto.getPincode())
                .build();
        return Mono.just(new EnrolByDocumentResponseDto(enrolProfileDto));
    }
}
