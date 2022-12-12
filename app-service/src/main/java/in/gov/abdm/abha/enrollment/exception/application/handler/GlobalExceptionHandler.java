package in.gov.abdm.abha.enrollment.exception.application.handler;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.exception.database.constraint.AccountNotFoundException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.InvalidRequestException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.ParentLinkingFailedException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.model.ErrorResponse;
import in.gov.abdm.abha.enrollment.exception.notification.FailedToSendNotificationException;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.enums.ClassLevelExceptionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private static final String RESPONSE_TIMESTAMP = "timestamp";
    private static final String CONTROLLER_ADVICE_EXCEPTION_CLASS = "API Request Body Exception : ";
    private static final String SEND_NOTIFICATION_EXCEPTION = "send notification Exception : ";
    private static final String EXCEPTIONS = "Exceptions : ";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * <p>
     * WebExchangeBindException exception is thrown after data binding
     * and validation failure.
     * This method returns BindingResult from WebExchangeBindException and then
     * compose list of error messages based on all rejected fields
     * </p>
     *
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
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }

    /**
     * handle exception on failing of database constraints
     *
     * @param ex
     * @return status code and error message
     */
    @ExceptionHandler(DatabaseConstraintFailedException.class)
    public ResponseEntity<ErrorResponse> dbConstraintFailed(DatabaseConstraintFailedException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(status, ex.getMessage()), status);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(FailedToSendNotificationException.class)
    public Map<String, Object> notificationExceptionHandler(FailedToSendNotificationException ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put(StringConstants.MESSAGE, ex.getMessage());
        log.info(SEND_NOTIFICATION_EXCEPTION, errorMap);
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }

    @ExceptionHandler(GenericExceptionMessage.class)
    public Map<String, Object> runtimeGenericExceptionHandler(GenericExceptionMessage ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put(StringConstants.MESSAGE, ex.getMessage());
        log.info(EXCEPTIONS, ex.getMessage());
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }

    /**
     * handling exception to show error message in case account doesn't exist with the xmluid
     * @param ex
     * @return
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> accountNotFound(AccountNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(status,ex.getMessage()),status);
    }
    
    /**
     * handling exception to show error message in case transaction doesn't exists 
     * or if it is expired
     * @param ex
     * @return
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> transactionNotFound(TransactionNotFoundException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(new ErrorResponse(status,ex.getMessage()),status);
    }
    
    /**
     * handling exception to show error message in case request body is un-expected 
     * @param ex
     * @return
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> invalidRequest(InvalidRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status,ex.getMessage()),status);
    }

    /**
     * handling exception to show error message incase validation fails while linking parent
     * @param ex
     * @return
     */
    @ExceptionHandler(ParentLinkingFailedException.class)
    public ResponseEntity<ErrorResponse> parentLinkingFailed(ParentLinkingFailedException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(new ErrorResponse(status,ex.getMessage()),status);
    }
}
