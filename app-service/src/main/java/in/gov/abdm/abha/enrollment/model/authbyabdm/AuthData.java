package in.gov.abdm.abha.enrollment.model.authbyabdm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

/**
 * It is a Pojo class Authdata
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthData {
    /**
     * It is authMethods
     */
    @JsonProperty("authMethods")
    public ArrayList<String> authMethods;
    /**
     * It is otp
     */
    @JsonProperty("otp")
    public Otp otp ;
}
