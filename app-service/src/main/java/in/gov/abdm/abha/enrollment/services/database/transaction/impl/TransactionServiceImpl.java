package in.gov.abdm.abha.enrollment.services.database.transaction.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    public static final String PARSER_EXCEPTION_OCCURRED_DURING_PARSING = "Parser Exception occurred during parsing :";
    public static final String EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB = "Exception in parsing Invalid value of DOB: {}";
    private DateFormat KYC_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    AbhaDBClient abhaDBClient;

    @Override
    public void mapTransactionWithEkyc(TransactionDto transactionDto, AadhaarUserKycDto kycData, String kycType) {
        //TODO set kyc photo in transaction
        //transactionDto.setKycPhoto(kycData.getPhoto() == null ? new byte[1] : kycData.getPhoto().getBytes());
        if (!StringUtils.isBlank(kycData.getPincode())) {
            transactionDto.setPincode(kycData.getPincode());
        }
        if (!StringUtils.isBlank(kycData.getBirthdate())) {
            transactionDto.setKycdob(kycData.getBirthdate());
            if (kycData.getBirthdate().length() > 4) {
                try {
                    LocalDate birthDate = KYC_DATE_FORMAT.parse(kycData.getBirthdate()).toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    transactionDto.setMonthOfBirth(String.valueOf(birthDate.getMonth().getValue()));
                    transactionDto.setDayOfBirth(String.valueOf(birthDate.getDayOfMonth()));
                    transactionDto.setYearOfBirth(String.valueOf(birthDate.getYear()));
                } catch (ParseException e) {
                    log.error(PARSER_EXCEPTION_OCCURRED_DURING_PARSING, e);
                } catch (Exception ex) {
                    log.error(EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB, kycData.getBirthdate());
                }
            } else if (kycData.getBirthdate().length() == 4) {
                transactionDto.setYearOfBirth(kycData.getBirthdate());
            }
        }

        transactionDto.setAddress(kycData.getAddress());
        transactionDto.setName(kycData.getName());
        transactionDto.setGender(kycData.getGender());
        transactionDto.setDistrictName(kycData.getDistrict());
        transactionDto.setCo(kycData.getCareOf());
        transactionDto.setHouse(kycData.getHouse());
        transactionDto.setLoc(kycData.getLocality());
        transactionDto.setVillageName(kycData.getVillageTownCity());
        transactionDto.setStateName(kycData.getState());
        transactionDto.setSubDistrictName(kycData.getSubDist());
        transactionDto.setTownName(kycData.getVillageTownCity());
        transactionDto.setXmluid(kycData.getSignature());
        transactionDto.setKycVerified(true);
        transactionDto.setKycReason(kycData.getReason());
        transactionDto.setKycReason(kycData.getStatus());
        transactionDto.setResponseCode(kycData.getResponseCode());
        transactionDto.setKycType(kycType);
    }

    @Override
    public String generateTransactionId(boolean isKYCTxn) {
        String transactionId;
        String host = System.getProperty("HOST", "H1");
        String prefix = "HID-NDHM-";
        if (isKYCTxn) {
            prefix = "UKC:" + prefix;
        }
        transactionId = prefix + host + "-" + Common.getTimeStamp(true);
        String clientId = null;
        //TODO find client id
        clientId = "abha";
        /*if (!StringUtils.isEmpty(HealthIdContextHolder.clientId())) {
            clientId = HealthIdContextHolder.clientId().replaceAll("_", "");
            clientId = clientId.length() > 8 ? clientId.substring(0, 8) : clientId;
        }*/
        transactionId = !clientId.isEmpty() ? transactionId.replace("NDHM", clientId) : transactionId;
        return transactionId;
    }

    @Override
    public Mono<TransactionDto> createTransactionEntity(TransactionDto transactionDto) {
        return abhaDBClient.addEntity(TransactionDto.class, transactionDto);
    }

    @Override
    public Mono<TransactionDto> findTransactionDetailsFromDB(String txnId) {
        return abhaDBClient.getEntityById(TransactionDto.class, txnId);
    }

    @Override
    public Mono<TransactionDto> updateTransactionEntity(TransactionDto transactionDto, String transactionId){
        return abhaDBClient.updateEntity(TransactionDto.class, transactionDto, transactionId);
    }
}
