package in.gov.abdm.abha.enrollment.model.procedure;

import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveAllDataRequest {

    private List<AccountDto> accounts;
    private List<HidPhrAddressDto> hidPhrAddress;
    private List<AccountAuthMethodsDto> accountAuthMethods;

}
