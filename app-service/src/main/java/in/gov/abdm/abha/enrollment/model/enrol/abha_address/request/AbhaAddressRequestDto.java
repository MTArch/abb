package in.gov.abdm.abha.enrollment.model.enrol.abha_address.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.AbhaId;
import in.gov.abdm.abha.enrollment.validators.annotations.Preferred;
import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AbhaAddressRequestDto {

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
    @Uuid
    @JsonProperty("txnId")
    private String txnId;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_ABHA_ADDRESS_FIELD)
    @AbhaId
    @JsonProperty("abhaAddress")
    private String preferredAbhaAddress;

    @NotNull(message = AbhaConstants.VALIDATION_ERROR_PREFERRED_FLAG)
    @Preferred
    @JsonProperty("preferred")
    private Integer preferred;
}
