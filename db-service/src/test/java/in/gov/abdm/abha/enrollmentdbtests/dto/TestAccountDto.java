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
        String kyc = accountDto.getVerificationType();
        HidPhrAddress hids = accountDto.getHidPhrAddress();
        kyc = accountDto.getHealthId();
        kyc = accountDto.getProfilePhoto();
        kyc = accountDto.getKycPhoto();
        kyc = accountDto.getAddress();
        kyc = accountDto.getSubDistrictName();
        LocalDateTime a = accountDto.getCreatedDate();
        kyc =  accountDto.getKycdob();
        kyc = accountDto.getHealthId();
        kyc = accountDto.getApiEndPoint();
        kyc = accountDto.getApiVersion();
        kyc = accountDto.getCmMigrated();
        kyc = accountDto.getYearOfBirth();
        kyc = accountDto.getXmluid();
        kyc = accountDto.getWardName();
        kyc = accountDto.getWardCode();
        kyc = accountDto.getVillageName();
        kyc = accountDto.getVillageCode();
        kyc = accountDto.getVerificationStatus();
        a = accountDto.getUpdateDate();
        AbhaType s = accountDto.getType();
        kyc = accountDto.getTownName();
        kyc = accountDto.getTownCode();
return accountDto;
    }
}
