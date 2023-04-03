package in.gov.abdm.abha.enrollment.model.link.parent.request;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.AbhaNumberChild;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildAbhaRequestDto {

    @JsonProperty("ABHANumber")
    @NotNull(message = AbhaConstants.INVALID_CHILD_ABHA_NUMBER)
    @AbhaNumberChild
    private String ABHANumber;
}
