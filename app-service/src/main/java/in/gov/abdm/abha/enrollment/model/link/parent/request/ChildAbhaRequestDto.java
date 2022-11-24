package in.gov.abdm.abha.enrollment.model.link.parent.request;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.AbhaNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildAbhaRequestDto {

    @JsonProperty("ABHANumber")
    @NotEmpty(message = AbhaConstants.INVALID_ABHA_NUMBER)
    @AbhaNumber
    private String ABHANumber;
}
