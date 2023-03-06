package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.error.ABDMError;
import in.gov.abdm.error.ErrorResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * It is General Utils class
 */
@UtilityClass
@Slf4j
public class GeneralUtils {

    public static final String PNG = "PNG";
    public static final String JPG = "JPG";
    public static final String JPEG = "JPEG";

    /**
     * Method to check if given String is palindrome
     *
     * @param str
     * @return true if String is a Palindrome.
     */
    public boolean isPalindrome(String str) {
        return str.equals(new StringBuffer(str).reverse().toString());
    }

    public String stringTrimmer(String str) {
        return StringUtils.isEmpty(str) ? str : str.trim();
    }

    public String generateRandomOTP() {
        return RandomStringUtils.randomNumeric(6);
    }

    public static Long getCurrentTime() {
        return System.currentTimeMillis() + (1000);
    }

    public boolean isOtpExpired(LocalDateTime currentDateTime, long expireTimeMin) {
        boolean isOTPExpire = false;
        if (Objects.nonNull(currentDateTime)) {
            isOTPExpire = expireTimeMin < TimeUnit.MILLISECONDS.toMinutes(getCurrentTime() - Common.dateOf(currentDateTime).getTime());
        }
        return isOTPExpire;
    }

    public String documentChecksum(String documentType, String documentId) {
        return DigestUtils.md5Hex(documentType.concat("-").concat(documentId));
    }

    public String removeSpecialChar(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    public double fileSize(String base64) {
        double sizeInBytes = 4 * Math.ceil((base64.length() / 3)) * 0.5624896334383812;
        double sizeInKb = sizeInBytes / 1024;
        return sizeInKb;
    }

    public boolean isImageFileFormat(String base64) {
        try {
            return ImageIO.read(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(base64))) != null;
        } catch (IOException e) {
            return false;
        }
    }
    public Mono<DataBuffer> prepareFilterExceptionResponse(ServerWebExchange exchange, ABDMError error) {
        return Mono.just(exchange.getResponse().bufferFactory()
                .wrap(new JSONObject(new ErrorResponse(error.getCode(), error.getMessage())).toString().getBytes()));
    }

}

