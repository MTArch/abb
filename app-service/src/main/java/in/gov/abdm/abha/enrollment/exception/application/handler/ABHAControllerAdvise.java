package in.gov.abdm.abha.enrollment.exception.application.handler;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.application.*;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.InvalidRequestException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.document.DocumentGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.idp.IdpGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.lgd.LgdGatewayUnavailableException;
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

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class ABHAControllerAdvise {

    private static final String FEIGN = "feign";
    private static final String MESSAGE = "\"message\":";
    private static final String CONTROLLER_ADVICE_EXCEPTION_CLASS = "API Request Body Exception : ";
    private static final String RESPONSE_TIMESTAMP = "timestamp";
    private static final String EXCEPTIONS = "Exceptions : ";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mono<ErrorResponse>> exception(Exception exception) {
        if (exception.getClass() == TransactionNotFoundException.class) {
            return handleTransactionNotFoundException();
        } else if (exception.getClass().getPackageName().equals(FEIGN) && exception.getMessage().contains(MESSAGE)) {
            return handleFienClientExceptions(exception);
        } else if (exception.getClass() == DatabaseConstraintFailedException.class) {
            return handleDatabaseConstraintFailedException();
        } else if (exception.getClass() == NotificationGatewayUnavailableException.class) {
            return handleNotificationGatewayUnavailableException();
        } else if (exception.getClass() == RedisConnectionFailureException.class) {
            return handleRedisConnectionFailureException();
        } else if (exception.getClass() == UnauthorizedUserToSendOrVerifyOtpException.class) {
            return handleUnauthorizedUserToSendOrVerifyOtpException();
        } else if (exception.getClass() == InvalidRequestException.class) {
            return handleInvalidRequestException();
        } else if (exception.getClass() == DocumentGatewayUnavailableException.class) {
            return handleDocumentGatewayUnavailableException();
        } else if (exception.getClass() == LgdGatewayUnavailableException.class) {
            return handleLgdGatewayUnavailableException();
        } else if (exception.getClass() == AbhaUnProcessableException.class) {
            return handleAbhaUnProcessableException(exception.getMessage());
        }else if (exception.getClass() == AbhaBadRequestException.class) {
            return handleAbhaBadRequestException(exception.getMessage());
        }else if (exception.getClass() == AbhaUnAuthorizedException.class) {
            return handleAbhaUnAuthorizedException(exception.getMessage());
        }else if (exception.getClass() == IdpGatewayUnavailableException.class) {
            return handleIdpGatewayUnavailableException();
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    prepareCustomErrorResponse(ABDMError.UNKNOWN_EXCEPTION.getCode(), ABDMError.UNKNOWN_EXCEPTION.getMessage())
            );
        }
    }

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

    private ResponseEntity<Mono<ErrorResponse>> handleDatabaseConstraintFailedException() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.DATABASE_UNAVAILABLE.getCode()
                                + ABDMError.DATABASE_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleNotificationGatewayUnavailableException() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.NOTIFICATION_SERVICE_UNAVAILABLE.getCode()
                                + ABDMError.NOTIFICATION_SERVICE_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleRedisConnectionFailureException() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.REDIS_SERVER_UNAVAILABLE.getCode()
                                + ABDMError.REDIS_SERVER_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleUnauthorizedUserToSendOrVerifyOtpException() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.EXCEEDED_MULTIPLE_OTP_REQUEST_OR_OTP_MATCH.getCode()
                                + ABDMError.EXCEEDED_MULTIPLE_OTP_REQUEST_OR_OTP_MATCH.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleInvalidRequestException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.BAD_REQUEST.getCode()
                                + ABDMError.BAD_REQUEST.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleDocumentGatewayUnavailableException() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.DOCUMENT_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.DOCUMENT_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleLgdGatewayUnavailableException() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ABDMControllerAdvise.handleException(
                        new Exception(ABDMError.LGD_GATEWAY_UNAVAILABLE.getCode()
                                + ABDMError.LGD_GATEWAY_UNAVAILABLE.getMessage())
                )
        );
    }

    private ResponseEntity<Mono<ErrorResponse>> handleIdpGatewayUnavailableException() {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
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

    private ResponseEntity<Mono<ErrorResponse>> handleAbhaUnProcessableException(String ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ABDMControllerAdvise.handleException(new Exception(ex)));
    }

    private ResponseEntity<Mono<ErrorResponse>> handleAbhaBadRequestException(String ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ABDMControllerAdvise.handleException(new Exception(ex)));
    }

    private ResponseEntity<Mono<ErrorResponse>> handleAbhaUnAuthorizedException(String ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ABDMControllerAdvise.handleException(new Exception(ex)));
    }

    private ResponseEntity<Mono<ErrorResponse>> handleFienClientExceptions(Exception exception) {
        String msg = (exception.getMessage().split("\"message\":")[1]);
        Exception wrapped = new Exception(ABDMError.BAD_REQUEST + StringConstants.COLON + msg);
        return ResponseEntity.badRequest().body(ABDMControllerAdvise.handleException(wrapped));
    }

    private Mono<ErrorResponse> prepareCustomErrorResponse(String errorCode, String errorMessage) {
        return Mono.just(new ErrorResponse(errorCode, errorMessage));
    }
}
