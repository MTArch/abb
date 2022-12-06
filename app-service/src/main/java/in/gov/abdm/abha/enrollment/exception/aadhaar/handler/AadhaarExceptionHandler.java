package in.gov.abdm.abha.enrollment.exception.aadhaar.handler;

import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarErrorCodes;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.utilities.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class AadhaarExceptionHandler {

    private static final String AADHAAR_EXCEPTION = "Aadhaar Exception : ";
    private static final String MESSAGE = "Message";
    private static final String RESPONSE_TIMESTAMP = "timestamp";

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(AadhaarExceptions.class)
    public Map<String, Object> handleAadhaarOtpException(AadhaarExceptions ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        String errorMessage;
        if (Arrays.stream(AadhaarErrorCodes.values()).anyMatch(v -> v.toString().equals("E_" + ex.getMessage()))) {
            errorMessage = AadhaarErrorCodes.valueOf("E_" + ex.getMessage()).getValue();
        } else {
            errorMessage = AadhaarErrorCodes.valueOf("OTHER_ERROR").getValue();
        }
        log.info(AADHAAR_EXCEPTION + ex.getMessage() + " : " + errorMessage);
        errorMap.put(MESSAGE, errorMessage);
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }
}
