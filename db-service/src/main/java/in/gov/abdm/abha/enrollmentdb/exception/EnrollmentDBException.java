package in.gov.abdm.abha.enrollmentdb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class EnrollmentDBException {
    private static final String MESSAGE = "message";
    private static final String EXCEPTION = "Exception : ";
    private static final String RESPONSE_TIMESTAMP = "RESPONSE_TIMESTAMP";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    /**
     *
     */

    /**
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    Mono<String> genericException(Exception e) {
        return Mono.just(e.getMessage());
    }

    @ExceptionHandler(GenericExceptionMessage.class)
    public Map<String, Object> runtimeGenericExceptionHandler(GenericExceptionMessage ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put(MESSAGE, ex.getMessage());
        log.info(EXCEPTION, ex.getMessage());
        errorMap.put(RESPONSE_TIMESTAMP, LocalDateTime.now().format(dateTimeFormatter));
        return errorMap;
    }
}
