package in.gov.abdm.abha.enrollment.exception.application.handler;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarErrorCodes;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.EnrolmentIdNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.*;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.document.DocumentDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.document.DocumentGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.idp.IdpGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.lgd.LgdGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.enums.ClassLevelExceptionConstants;
import in.gov.abdm.controller.ABDMControllerAdvise;
import in.gov.abdm.error.ABDMError;
import in.gov.abdm.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class ABHAControllerAdvise {

    private static final String FEIGN = "feign";
    private static final String BAD_REQUEST = "BAD_REQUEST";
    private static final String MESSAGE = "\"message\":";
    private static final String CONTROLLER_ADVICE_EXCEPTION_CLASS = "API Request Body Exception : ";
    private static final String RESPONSE_TIMESTAMP = "timestamp";
    private static final String EXCEPTIONS = "Exceptions : ";
    private static final String AADHAAR_ERROR_PREFIX = "UIDAI Error code : ";
    private static final String TRACKING_ID = "Tracking Id : ";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mono<ErrorResponse>> exception(Exception exception) {
        String trackingId = UUID.randomUUID().toString();
        log.error(trackingId + StringConstants.COLON + "Message : " + exception.getMessage() + StringConstants.COLON + exception.getStackTrace()[0].toString());
        if (exception.getClass() == TransactionNotFoundException.class) {
            return handleTransactionNotFoundException();
        } else if (exception.getClass() == AbhaDBGatewayUnavailableException.class) {
            return handleDatabaseConstraintFailedException(ABDMError.ABHA_DB_SERVICE_UNAVAILABLE);
        } else if (exception.getClass() == NotificationDBGatewayUnavailableException.class) {
            return handleDatabaseConstraintFailedException(ABDMError.NOTIFICATION_DB_SERVICE_UNAVAILABLE);
        } else if (exception.getClass() == DocumentDBGatewayUnavailableException.class) {
            return handleDatabaseConstraintFailedException(ABDMError.DOCUMENT_DB_GATEWAY_UNAVAILABLE);
        } else if (exception.getClass() == AadhaarGatewayUnavailableException.class) {
            return handleAadhaarGatewayUnavailableException();
        } else if (exception.getClass() == NotificationGatewayUnavailableException.class) {
            return handleNotificationGatewayUnavailableException();
        } else if (exception.getClass() == RedisConnectionFailureException.class) {
            return handleRedisConnectionFailureException();
        } else if (exception.getClass() == UnauthorizedUserToSendOrVerifyOtpException.class) {
            return handleUnauthorizedUserToSendOrVerifyOtpException();
        } else if (exception.getClass() == DocumentGatewayUnavailableException.class) {
            return handleDocumentGatewayUnavailableException();
        } else if (exception.getClass() == LgdGatewayUnavailableException.class) {
            return handleLgdGatewayUnavailableException();
        } else if (exception.getClass() == IdpGatewayUnavailableException.class) {
            return handleIdpGatewayUnavailableException();
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
        } else if (exception.getClass() != NullPointerException.class && exception.getMessage().contains(BAD_REQUEST)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handleAbdmException(ABDMError.BAD_REQUEST));
        } else if (exception.getClass() == EnrolmentIdNotFoundException.class) {
            return handleEnrolmentIdNotFoundException();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    prepareCustomErrorResponse(ABDMError.UNKNOWN_EXCEPTION.getCode(), ABDMError.UNKNOWN_EXCEPTION.getMessage() + StringConstants.COLON + TRACKING_ID + trackingId)
            );
        }
    }

    private Mono<ErrorResponse> handleAbdmException(ABDMError error) {
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
        log.info(errorMessage);
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
        log.info(CONTROLLER_ADVICE_EXCEPTION_CLASS + errorMap);
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }


    private ResponseEntity<Mono<ErrorResponse>> handleTransactionNotFoundException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.INVALID_TRANSACTION_ID.getCode()
                                + ABDMError.INVALID_TRANSACTION_ID.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleDatabaseConstraintFailedException(ABDMError abdmError) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(abdmError.getCode() + abdmError.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleNotificationGatewayUnavailableException() {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.NOTIFICATION_SERVICE_UNAVAILABLE.getCode()
                                + ABDMError.NOTIFICATION_SERVICE_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleAadhaarGatewayUnavailableException() {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.AADHAAR_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.AADHAAR_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleRedisConnectionFailureException() {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.REDIS_SERVER_UNAVAILABLE.getCode()
                                + ABDMError.REDIS_SERVER_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleUnauthorizedUserToSendOrVerifyOtpException() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.EXCEEDED_MULTIPLE_OTP_REQUEST_OR_OTP_MATCH.getCode()
                                + ABDMError.EXCEEDED_MULTIPLE_OTP_REQUEST_OR_OTP_MATCH.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleDocumentGatewayUnavailableException() {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.DOCUMENT_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.DOCUMENT_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    public ResponseEntity<Mono<ErrorResponse>> handleEnrolmentIdNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.ENROLLMENT_ID_NOT_FOUND.getCode()
                                + ABDMError.ENROLLMENT_ID_NOT_FOUND.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleLgdGatewayUnavailableException() {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.LGD_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.LGD_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleIdpGatewayUnavailableException() {
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
        LinkedHashMap<String, String> errorMap = ex.getErrors();
        log.info(EXCEPTIONS + ex.getErrors());
        errorMap.put(RESPONSE_TIMESTAMP, Common.timeStampWithT());
        return errorMap;
    }

    private ResponseEntity<Mono<ErrorResponse>> handleAbhaExceptions(HttpStatus httpStatus, String ex) {
        return ResponseEntity.status(httpStatus).body(ABDMControllerAdvise.handleException(new Exception(ex)));
    }

    private ResponseEntity<Mono<ErrorResponse>> handleFienClientExceptions(Exception exception) {
        String msg = (exception.getMessage());
        Exception wrapped = new Exception(ABDMError.FEIGN_EXCEPTION.getCode() + msg.replace(":", "-"));
        return ResponseEntity.internalServerError().body(ABDMControllerAdvise.handleException(wrapped));
    }

    private Mono<ErrorResponse> prepareCustomErrorResponse(String errorCode, String errorMessage) {
        return Mono.just(new ErrorResponse(errorCode, errorMessage));
    }
}
