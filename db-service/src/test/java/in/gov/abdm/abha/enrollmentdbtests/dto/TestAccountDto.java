package in.gov.abdm.abha.enrollmentdbtests.dto;

import in.gov.abdm.abha.enrollmentdb.enums.AbhaType;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
public class TestAccountDto {
    public AccountDto accountDto(){
        AccountDto accountDto=new AccountDto();
        accountDto.setNewAccount(true);
        accountDto.setVerificationType("");
        accountDto.setHidPhrAddress(new HidPhrAddress());
        accountDto.setHealthId("");
        accountDto.setProfilePhoto("");
        accountDto.setKycPhoto("");
        accountDto.setAddress("");
        accountDto.setSubDistrictName("");
        accountDto.setCreatedDate(LocalDateTime.now());
        accountDto.setHealthId("");
        accountDto.setApiEndPoint("");
        accountDto.setKycdob("");
        accountDto.setApiVersion("");
        accountDto.setCmMigrated("");
        accountDto.setYearOfBirth("");
        accountDto.setXmluid("");
        accountDto.setWardName("");
        accountDto.setWardCode("");
        accountDto.setVillageName("");
        accountDto.setVillageCode("");
        accountDto.setVerificationStatus("");
        accountDto.setUpdateDate(LocalDateTime.now());
        accountDto.setType(AbhaType.CHILD);
        accountDto.setTownName("");
        accountDto.setTownCode("");
        accountDto.getVerificationType();
        accountDto.getHidPhrAddress();
        accountDto.getHealthId();
        accountDto.getProfilePhoto();
        accountDto.getKycPhoto();
        accountDto.getAddress();
        accountDto.getSubDistrictName();
        accountDto.getCreatedDate();
        String kyc = accountDto.getKycdob();
        accountDto.getHealthId();
        accountDto.getApiEndPoint();
        accountDto.getApiVersion();
        accountDto.getCmMigrated();
        accountDto.getYearOfBirth();
        accountDto.getXmluid();
        accountDto.getWardName();
        accountDto.getWardCode();
        accountDto.getVillageName();
        accountDto.getVillageCode();
        accountDto.getVerificationStatus();
        accountDto.getUpdateDate();
        accountDto.getType();
        accountDto.getTownName();
        accountDto.getTownCode();
return accountDto;
    }
}
