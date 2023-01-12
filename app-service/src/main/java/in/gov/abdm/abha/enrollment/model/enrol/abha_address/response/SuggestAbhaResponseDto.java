package in.gov.abdm.abha.enrollment.model.enrol.abha_address.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuggestAbhaResponseDto {

    private String txnId;
    private List<String> abhaAddressList;
}
