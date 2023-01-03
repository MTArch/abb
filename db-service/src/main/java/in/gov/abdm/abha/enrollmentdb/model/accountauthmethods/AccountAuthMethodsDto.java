package in.gov.abdm.abha.enrollmentdb.model.accountauthmethods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountAuthMethodsDto {
    private String healthIdNumber;
    private String authMethods;
}
