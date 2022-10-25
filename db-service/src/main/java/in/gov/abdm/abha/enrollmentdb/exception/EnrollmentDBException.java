package in.gov.abdm.abha.enrollmentdb.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 *
 */
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EnrollmentDBException {
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(EnrollmentDBException.class);

    /**
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    Mono<String> genericException(Exception e) {
        return Mono.just(e.getMessage());
    }
}
