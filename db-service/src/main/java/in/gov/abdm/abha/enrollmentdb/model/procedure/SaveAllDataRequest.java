package in.gov.abdm.abha.enrollmentdb.model.procedure;

import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveAllDataRequest {

    private List<Accounts> accounts;
    private List<HidPhrAddress> hidPhrAddress;
    private List<AccountAuthMethods> accountAuthMethods;

}
