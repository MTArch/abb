package in.gov.abdm.abha.enrollment.model.enrol.abha_address.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AbhaAddressResponseDto {

    private String txnId;
    private String healthIdNumber;
    private String preferredAbhaAddress;
}
