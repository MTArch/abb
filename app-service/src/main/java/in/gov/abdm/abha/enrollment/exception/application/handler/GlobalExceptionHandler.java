package in.gov.abdm.abha.enrollment.exception.application.handler;

import in.gov.abdm.abha.enrollment.validators.enums.ClassLevelExceptionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This RestControllerAdvice to handle exceptions and error codes while validating input request fields for generate otp flow
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Constants for logging
     */
    public static final String RESPONSE_TIMESTAMP = "timestamp";
    public static final String CONTROLLER_ADVICE_EXCEPTION_CLASS = "WebExchangeBindException :{}";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * <p>
     * WebExchangeBindException exception is thrown after data binding
     * and validation failure.
     * This method returns BindingResult from WebExchangeBindException and then
     * compose list of error messages based on all rejected fields
     * </p>
     * @param ex
     * @return list of error messages after analysis of binding and validation errors.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public Map<String, Object> handleInvalidFieldException(WebExchangeBindException ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();

        if (!ex.getAllErrors().isEmpty()) {
            ex.getAllErrors().forEach(error -> {
                {
                    errorMap.put(Arrays.stream(ClassLevelExceptionConstants.values())
                            .filter(v -> v.getValue().equals(error.getDefaultMessage()))
                            .findAny()
                            .get().toString(), error.getDefaultMessage());
                }
            });
        }

        log.info(CONTROLLER_ADVICE_EXCEPTION_CLASS, errorMap);
        errorMap.put(RESPONSE_TIMESTAMP, LocalDateTime.now().format(dateTimeFormatter));
        return errorMap;
    }
}
