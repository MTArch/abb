package in.gov.abdm.abha.enrollment.model.link.parent.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.AbhaNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@AbhaNumber
public class ChildAbhaRequestDto {

    @JsonProperty("ABHANumber")
    @NotNull(message = AbhaConstants.VALIDATION_NULL_ABHA_NUMBER)
    private String ABHANumber;
}
