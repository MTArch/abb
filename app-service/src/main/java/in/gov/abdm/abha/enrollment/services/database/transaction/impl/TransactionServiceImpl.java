package in.gov.abdm.abha.enrollment.services.database.transaction.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBTransactionFClient;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    public static final String PARSER_EXCEPTION_OCCURRED_DURING_PARSING = "Parser Exception occurred during parsing :";
    public static final String EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB = "Exception in parsing Invalid value of DOB: {}";
    private DateFormat KYC_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    AbhaDBTransactionFClient abhaDBTransactionFClient;

    @Override
    public void mapTransactionWithEkyc(TransactionDto transactionDto, AadhaarUserKycDto kycData, String kycType) {
		transactionDto.setKycPhoto((kycData.getPhoto() == null || kycData.getPhoto().isEmpty())
				? Base64.getEncoder().encodeToString(new byte[1])
				: kycData.getPhoto());
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
                    log.error(ex.getMessage());
                    log.error(EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB, kycData.getBirthdate());
                }
            } else if (kycData.getBirthdate().length() == 4) {
                transactionDto.setYearOfBirth(kycData.getBirthdate());
            }
        }

        transactionDto.setName(kycData.getName());
        transactionDto.setGender(kycData.getGender());

        transactionDto.setTownName(kycData.getVillageTownCity());
        transactionDto.setXmluid(kycData.getSignature());
        transactionDto.setKycVerified(true);
        transactionDto.setKycReason(kycData.getReason());
        transactionDto.setKycStatus(kycData.getStatus());
        transactionDto.setResponseCode(kycData.getResponseCode());
        transactionDto.setKycType(kycType);

        transactionDto.setCo(Common.getStringIgnoreNull(kycData.getCareOf()));
        String house = !Common.getStringIgnoreNull(kycData.getHouse()).isEmpty() ? Common.getStringIgnoreNull(kycData.getHouse()) +StringConstants.COMMA_SPACE : StringConstants.EMPTY;
        transactionDto.setHouse(house + Common.getStringIgnoreNull(kycData.getStreet()));
        transactionDto.setLm(Common.getStringIgnoreNull(kycData.getLandmark()));
        transactionDto.setLoc(Common.getStringIgnoreNull(kycData.getLocality()));
        transactionDto.setVillageName(Common.getStringIgnoreNull(kycData.getVillageTownCity()));
        transactionDto.setSubDistrictName(Common.getStringIgnoreNull(kycData.getSubDist()));
        transactionDto.setDistrictName(Common.getStringIgnoreNull(kycData.getDistrict()));
        transactionDto.setStateName(Common.getStringIgnoreNull(kycData.getState()));

        transactionDto.setAddress( Common.getByCommaIgnoreNull(transactionDto.getHouse())
                + Common.getByCommaIgnoreNull(transactionDto.getLm())
                + Common.getByCommaIgnoreNull(transactionDto.getLoc())
                + Common.getByCommaIgnoreNull(transactionDto.getVillageName())
                + Common.getByCommaIgnoreNull(transactionDto.getSubDistrictName())
                + Common.getByCommaIgnoreNull(transactionDto.getDistrictName())
                + transactionDto.getStateName()
        );
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
        return abhaDBTransactionFClient.createTransaction(transactionDto)
                .onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<TransactionDto> findTransactionDetailsFromDB(String txnId) {
        return abhaDBTransactionFClient.getTransactionByTxnId(txnId)
                .onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<TransactionDto> updateTransactionEntity(TransactionDto transactionDto, String transactionId) {
        return abhaDBTransactionFClient.updateTransactionById( transactionDto, transactionId)
                .onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<ResponseEntity<Mono<Void>>> deleteTransactionEntity(String transactionId) {
        return abhaDBTransactionFClient.deleteTransactionByTxnId(transactionId)
                .onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
    }
}
