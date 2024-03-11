package in.gov.abdm.abha.enrollment.exception.application.handler;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarErrorCodes;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBException;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.EnrolmentIdNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.*;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.document.DocumentDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.document.DocumentGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.hidbenefit.BenefitNotFoundException;
import in.gov.abdm.abha.enrollment.exception.idp.IdpGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.lgd.LgdGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.enums.ClassLevelExceptionConstants;
import in.gov.abdm.abha.profile.exception.application.UnAuthorizedException;
import in.gov.abdm.controller.ABDMControllerAdvise;
import in.gov.abdm.error.ABDMError;
import in.gov.abdm.error.Error;
import in.gov.abdm.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.*;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;
import static in.gov.abdm.abha.profile.constants.AbhaConstants.*;
import static org.apache.logging.log4j.util.Strings.EMPTY;


@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class ABHAControllerAdvise {

    private static final String FEIGN = "feign";
    private static final String BAD_REQUEST = "BAD_REQUEST";
    private static final String CONTROLLER_ADVICE_EXCEPTION_CLASS = "API Request Body Exception : ";
    private static final String RESPONSE_TIMESTAMP = "timestamp";
    private static final String EXCEPTIONS = "Exceptions : ";
    private static final String AADHAAR_ERROR_PREFIX = "UIDAI Error code : ";
    private static final String TRACKING_ID = "Tracking Id : ";
    private static final String CODE = "code";
    private static final String MESSAGE_KEY = "message";
    private static final String PROCEDURE_ERROR_CODE = "\\[P0001]";
    private static final String REPLACE_REGEX = "[\\],\"]";


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mono<ErrorResponse>> exception(Exception exception) {
        String trackingId = UUID.randomUUID().toString();
        log.error(trackingId + StringConstants.COLON + "Message : ", exception);
        if (exception.getClass() == AbhaDBGatewayUnavailableException.class) {
            return handleDatabaseConstraintFailedException(ABDMError.ABHA_DB_SERVICE_UNAVAILABLE);
        } else if (exception.getClass() == NotificationDBGatewayUnavailableException.class) {
            return handleDatabaseConstraintFailedException(ABDMError.NOTIFICATION_DB_SERVICE_UNAVAILABLE);
        } else if (exception.getClass() == DocumentDBGatewayUnavailableException.class) {
            return handleDatabaseConstraintFailedException(ABDMError.DOCUMENT_DB_GATEWAY_UNAVAILABLE);
        } else if (exception.getClass() == AbhaUnProcessableException.class) {
            return handleAbhaExceptions(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
        } else if (exception.getClass() == AbhaBadRequestException.class) {
            return handleAbhaExceptions(HttpStatus.BAD_REQUEST, exception.getMessage());
        } else if (exception.getClass() == AbhaUnAuthorizedException.class) {
            return handleAbhaExceptions(HttpStatus.UNAUTHORIZED, exception.getMessage());
        } else if (exception.getClass() == AbhaOkException.class) {
            return handleAbhaExceptions(HttpStatus.OK, exception.getMessage());
        } else if (exception.getClass() == AbhaConflictException.class) {
            return handleAbhaExceptions(HttpStatus.CONFLICT, exception.getMessage());
        } else if (exception.getClass().getPackageName().contains(FEIGN)) {
            return handleFienClientExceptions(exception);
        } else if (exception.getClass() == AbhaNotFountException.class) {
            return handleAbhaExceptions(HttpStatus.NOT_FOUND, exception.getMessage());
        } else if (exception.getClass() == AbhaDBException.class) {
            return handleAbhaDBExceptions(exception.getMessage());
        } else if (exception.getClass() != NullPointerException.class && exception.getMessage().contains(BAD_REQUEST)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handleAbdmException(ABDMError.BAD_REQUEST));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    prepareCustomErrorResponse(ABDMError.UNKNOWN_EXCEPTION.getCode(), ABDMError.UNKNOWN_EXCEPTION.getMessage() + StringConstants.COLON + TRACKING_ID + trackingId)
            );
        }
    }

    private Mono<ErrorResponse> handleAbdmException(ABDMError error) {
        log.error(error.getMessage());
        return ABDMControllerAdvise.handleException(new Exception(error.getCode() + error.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(AadhaarExceptions.class)
    public Mono<ErrorResponse> handleAadhaarOtpException(AadhaarExceptions ex) {
        String errorMessage;
        if (Arrays.stream(AadhaarErrorCodes.values()).anyMatch(v -> v.toString().equals("E_" + ex.getMessage()))) {
            errorMessage = AADHAAR_ERROR_PREFIX + ex.getMessage() + StringConstants.COLON + AadhaarErrorCodes.valueOf("E_" + ex.getMessage()).getValue();
        } else {
            errorMessage = AADHAAR_ERROR_PREFIX + ex.getMessage() + StringConstants.COLON + AadhaarErrorCodes.valueOf("OTHER_ERROR").getValue();
        }
        log.error(errorMessage);
        return prepareCustomErrorResponse(ABDMError.AADHAAR_EXCEPTIONS.getCode().split(":")[0], errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public Map<String, Object> handleInvalidFieldException(WebExchangeBindException ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();

        if (!ex.getAllErrors().isEmpty()) {
            ex.getAllErrors().forEach(error -> errorMap.put(Arrays.stream(ClassLevelExceptionConstants.values())
                    .filter(v -> v.getValue().equals(error.getDefaultMessage()))
                    .findAny()
                    .get().toString(), error.getDefaultMessage()));
        }
        log.error(CONTROLLER_ADVICE_EXCEPTION_CLASS + errorMap);
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    private ResponseEntity<Mono<ErrorResponse>> handleTransactionNotFoundException() {
        log.error(ABDMError.INVALID_TRANSACTION_ID.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.INVALID_TRANSACTION_ID.getCode()
                                + ABDMError.INVALID_TRANSACTION_ID.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleDatabaseConstraintFailedException(ABDMError abdmError) {
        log.error(abdmError.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(abdmError.getCode() + abdmError.getMessage())
                )
        );
    }

    @ExceptionHandler(NotificationGatewayUnavailableException.class)
    private ResponseEntity<Mono<ErrorResponse>> handleNotificationGatewayUnavailableException() {
        log.error(ABDMError.NOTIFICATION_SERVICE_UNAVAILABLE.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.NOTIFICATION_SERVICE_UNAVAILABLE.getCode()
                                + ABDMError.NOTIFICATION_SERVICE_UNAVAILABLE.getMessage())
                )
        );
    }

    @ExceptionHandler(AadhaarGatewayUnavailableException.class)
    private ResponseEntity<Mono<ErrorResponse>> handleAadhaarGatewayUnavailableException(Exception e) {
        log.error(e.getMessage(), e.fillInStackTrace());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.AADHAAR_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.AADHAAR_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    private ResponseEntity<Mono<ErrorResponse>> handleRedisConnectionFailureException() {
        log.error(ABDMError.REDIS_SERVER_UNAVAILABLE.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.REDIS_SERVER_UNAVAILABLE.getCode()
                                + ABDMError.REDIS_SERVER_UNAVAILABLE.getMessage())
                )
        );
    }

    @ExceptionHandler(UnauthorizedUserToSendOrVerifyOtpException.class)
    private ResponseEntity<Mono<ErrorResponse>> handleUnauthorizedUserToSendOrVerifyOtpException() {
        log.error(ABDMError.EXCEEDED_MULTIPLE_OTP_REQUEST_OR_OTP_MATCH.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.EXCEEDED_MULTIPLE_OTP_REQUEST_OR_OTP_MATCH.getCode()
                                + ABDMError.EXCEEDED_MULTIPLE_OTP_REQUEST_OR_OTP_MATCH.getMessage())
                )
        );
    }

    @ExceptionHandler(DocumentGatewayUnavailableException.class)
    private ResponseEntity<Mono<ErrorResponse>> handleDocumentGatewayUnavailableException() {
        log.error(ABDMError.DOCUMENT_GATEWAY_UNAVAILABLE.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.DOCUMENT_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.DOCUMENT_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    @ExceptionHandler(EnrolmentIdNotFoundException.class)
    public ResponseEntity<Mono<ErrorResponse>> handleEnrolmentIdNotFoundException() {
        log.error(ENROLLMENT_NOT_FOUND_EXCEPTION_MESSAGE);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.ENROLLMENT_ID_NOT_FOUND.getCode()
                                + ENROLLMENT_NOT_FOUND_EXCEPTION_MESSAGE)
                )
        );
    }

    @ExceptionHandler(LgdGatewayUnavailableException.class)
    private ResponseEntity<Mono<ErrorResponse>> handleLgdGatewayUnavailableException() {
        log.error(ABDMError.LGD_GATEWAY_UNAVAILABLE.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.LGD_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.LGD_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    @ExceptionHandler(IdpGatewayUnavailableException.class)
    private ResponseEntity<Mono<ErrorResponse>> handleIdpGatewayUnavailableException() {
        log.error(ABDMError.IDP_GATEWAY_UNAVAILABLE.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.IDP_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.IDP_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Map<String, String> runtimeBadRequestHandler(BadRequestException ex) {
        Map<String, String> errorMap = ex.getErrors();
        log.error(EXCEPTIONS + ex.getErrors());
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }

    private ResponseEntity<Mono<ErrorResponse>> handleAbhaExceptions(HttpStatus httpStatus, String ex) {
        log.error(EXCEPTIONS + ex);
        return ResponseEntity.status(httpStatus).body(ABDMControllerAdvise.handleException(new Exception(ex)));
    }

    private ResponseEntity<Mono<ErrorResponse>> handleFienClientExceptions(Exception exception) {
        String msg = (exception.getMessage());
        log.error(EXCEPTIONS + msg);
        Exception wrapped = new Exception(ABDMError.FEIGN_EXCEPTION.getCode() + msg.replace(":", "-"));
        return ResponseEntity.internalServerError().body(ABDMControllerAdvise.handleException(wrapped));
    }

    private Mono<ErrorResponse> prepareCustomErrorResponse(String errorCode, String errorMessage) {
        return Mono.just(new ErrorResponse(new Error(errorCode, errorMessage)));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Map<String, Object> invalidRequest(ServerWebInputException ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        String message = ex.getMessage();
        message = message != null ? message : "";
        if (message.contains(SCOPES)) {
            errorMap.put(StringConstants.MESSAGE, INVALID_SCOPE);
        } else if (message.contains(AUTH_METHOD)) {
            errorMap.put(StringConstants.MESSAGE, INVALID_AUTH_METHODS);
        } else if (message.contains(REASONS)) {
            errorMap.put(StringConstants.MESSAGE, INVALID_REASON);
        }
        Optional.ofNullable(ex)
                .map(Throwable::getMessage)
                .filter(msg -> msg.contains("F-token"))
                .ifPresentOrElse(msg -> {
                    errorMap.put(CODE, ABDMError.INVALID_F_TOKEN.getCode().split(":")[0]);
                    errorMap.put(MESSAGE_KEY, ABDMError.INVALID_F_TOKEN.getMessage());
                    log.error(EXCEPTIONS + msg);
                }, () -> Optional.ofNullable(ex)
                        .map(Throwable::getMessage)
                        .ifPresent(msg -> {
                            errorMap.put(MESSAGE_KEY, ABDMError.BAD_REQUEST.getMessage());
                            errorMap.put(CODE, ABDMError.BAD_REQUEST.getCode().split(":")[0]);
                            log.error(EXCEPTIONS + msg);
                        }));
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }

    private ResponseEntity<Mono<ErrorResponse>> handleAbhaDBExceptions(String ex) {
        log.error(EXCEPTIONS + ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ABDMControllerAdvise.handleException(new Exception(ex.split(PROCEDURE_ERROR_CODE)[1].replaceAll(REPLACE_REGEX, EMPTY))));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BenefitNotFoundException.class)
    public Map<String, Object> benefitNotFoundException(BenefitNotFoundException ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put(StringConstants.MESSAGE, ex.getMessage());
        log.error(EXCEPTIONS + ex.getMessage());
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizedException.class)
    public Map<String, Object> unAuthorizedException(UnAuthorizedException ex) {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put(in.gov.abdm.abha.profile.constants.StringConstants.MESSAGE, ex.getMessage());
        log.error(EXCEPTIONS + ex.getMessage());
        errorMap.put(RESPONSE_TIMESTAMP, in.gov.abdm.abha.profile.utilities.Common.timeStampWithT());
        return errorMap;
    }
}
