package in.gov.abdm.abha.enrollment.utilities.argon2;

//import com.password4j.Password;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Argon2 {
    public String encode(String value) {
        return "";//Password.hash(value).addRandomSalt().withArgon2().getResult();
    }

    public boolean verify(String value, String valueToMatch) {
        return true;//Password.check(value, valueToMatch).withArgon2();
    }
}
