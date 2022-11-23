package in.gov.abdm.abha.enrollment.utilities.argon2;

import com.password4j.Password;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Argon2Util {
    public String encode(String value) {
        return Password.hash(value).addRandomSalt(2).withArgon2().getResult();
    }

    public boolean verify(String dbValue, String valueToMatch) {
        return Password.check(valueToMatch, dbValue).withArgon2();
    }
}
