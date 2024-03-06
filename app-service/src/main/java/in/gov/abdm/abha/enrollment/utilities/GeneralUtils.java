package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.error.ABDMError;
import in.gov.abdm.error.Error;
import in.gov.abdm.error.ErrorResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.json.JSONObject;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * It is General Utils class
 */
@UtilityClass
@Slf4j
public class GeneralUtils {

    public static final String PNG = "PNG";
    public static final String JPG = "JPG";
    public static final String JPEG = "JPEG";
    private static final String[] ALLOWED_IMAGE_EXTENSION = {"png", "jpeg", "jpg", "pdf"};
    public static final String DD_MM_YYYY = "dd-MM-yyyy";
    public static final String D_M_YYYY = "d-M-yyyy";

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

    public String formatDlNumber(String dlNumber) {
        int zeros = 7 - (dlNumber.substring(9).length());
        String zeroString = "0".repeat(Math.max(0, zeros));
        return dlNumber.substring(0, 9) + zeroString + dlNumber.substring(9);
    }

    @SuppressWarnings("java:S2185")
    /**
     * Math.ceil required
     */
    public double fileSize(String base64) {
        double sizeInBytes = 4 * Math.ceil((base64.length() / 3)) * 0.5624896334383812;
        return sizeInBytes / 1024;
    }

    public boolean isImageFileFormat(String base64) {
        try {
            return ImageIO.read(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(base64))) != null;
        } catch (IOException e) {
            log.error("Error while parsing the image file format", e);
            return false;
        }
    }

    public boolean isFileFormat(String base64) {
        byte[] decodedBytes;
        try {
            decodedBytes = Base64.getDecoder().decode(base64);
        } catch (Exception e) {
            return false;
        }
        String contentType = new Tika().detect(decodedBytes);
        String imageExtension = Arrays.stream(contentType.split("/")).collect(Collectors.toList()).get(1);
        List<String> imageExtensions = Arrays.asList(ALLOWED_IMAGE_EXTENSION);
        return imageExtensions.contains(imageExtension);

    }

    public boolean isValidAadhaarNumber(String aadhaarNumber) {
        return VerhoeffAlgorithm.validateVerhoeff(aadhaarNumber);
    }

    public Mono<DataBuffer> prepareFilterExceptionResponse(ServerWebExchange exchange, ABDMError error) {

        return Mono.just(exchange.getResponse().bufferFactory()
                .wrap(new JSONObject(new Error(error.getCode().split(":")[0], error.getMessage())).toString().getBytes()));
    }

    public LocalDateTime parseStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(D_M_YYYY);
        try {
            return LocalDate.parse(date, formatter).atStartOfDay();
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(date, formatter2).atStartOfDay();
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}

