package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.FIELD_BLANK_ERROR_MSG;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * It is Data Transfer Object for AuthMethod
 */
public class AuthMethodsDto {
    /**
     * 6 digit OTP
     */

    //@ApiModelProperty(example = "sw1uD+gpv3fj6NHBNhtcII3GksVtkLT9bvcz0svYDyUt/x3jTtedXSYgw4b90GTwfLfs1eow056VsOw9HFS/wB8uH5Ysx+QzpL7PxmAY1WOHwOj04sPKN6Dw8XY8vcXovtvZc1dUB+TPAlGGPNu8iqMVPetukysjRxgbNdLLKMxn46rIRb8NieeyuDx1EHa90jJP9KwKGZdsLr08BysrmMJExzTO9FT93CzoNg50/nxzaQgmkBSbu9D8DxJm7XrLzWSUB05YCknHbokm4iXwyYBsrmfFDE/xCDfzYPhYyhtEmOi4J/GMp+lO+gAHQFQtxkIADhoSR8WXGcAbCUj7uTjFsBU/tc+RtvSotso4FXy8v+Ylzj28jbFTmmOWyAwYi9pThQjXnmRnq43dVdd5OXmxIII6SXs0JzoFvKwSk7VxhuLIRYzKqrkfcnWMrrmRgE8xZ6ZLft6O3IeiHb9WA8b/6/qO8Hdd17FKsSF6te59gSpoajS0FtQIgFn/c+NHzQYo5ZdsuRGM9v+bhHTInI=",
    // dataType="String",name="otp",value = "Encrypted Mobile OTP.",required = true)
    @NotBlank
//    @OtpValue(encrypted = true)
    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    private String otp;
    /**
     * It Is Pi
     */
    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    private String pi;
}
