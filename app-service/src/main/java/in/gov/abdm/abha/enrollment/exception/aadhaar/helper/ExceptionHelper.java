package in.gov.abdm.abha.enrollment.exception.aadhaar.helper;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.aadhaar.BusinessException;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ApiError;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ErrorAttribute;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ErrorCode;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ErrorDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Exception helper
 * helps to prepare aadhaar exception to client response
 */
@Component
@AllArgsConstructor
@Slf4j
public class ExceptionHelper {

    /**
     * constants for logging
     */
    private static final String ERROR_KEY_FORMAT = "error.code.";
    private static final String CODE_DEFAULT_CODE = "$$";
    public static final String NO_ERROR_CODE_FOUND_FOR = "No Error code found for {}.";
    public static final String NO_MESSAGE_FOUND_FOR = "No Message found for {}.";

    /**
     * object to fetch messages from message property file
     */
    private MessageSource messageSource;

    /**
     * accepts the exception and find out the error code and message
     *
     * @param exception
     * @param locale
     * @return
     */
    public ApiError apiError(BusinessException exception, Locale locale) {
        String message = getMessage(exception.getCode(), exception, locale);
        if (Objects.nonNull(exception.getAttribute()) && !StringUtils.isEmpty(exception.getAttribute().getKey())
                && !StringUtils.isEmpty(exception.getAttribute().getValue())) {
            message = message.replaceAll(StringConstants.HASH + exception.getAttribute().getKey(), exception.getAttribute().getValue());
        } else {
            if (StringUtils.isEmpty(message)) {
                message = exception.getMessage();
            }

        }
        ErrorDetails errorDetails = ErrorDetails.builder().code(exception.getCode().code()).message(message)
                .attribute(exception.getAttribute()).build();

        return ApiError.builder().code(ErrorCode.BUSINESS_EXCEPTION.code()).message(getMessage(ErrorCode.BUSINESS_EXCEPTION, locale))
                .details(Collections.singletonList(errorDetails)).build();
    }

    /**
     * find actual message from message property file
     *
     * @param code
     * @param locale
     * @return
     */
    public ApiError apiError(ErrorCode code, Locale locale) {
        return ApiError.builder().code(code.code()).message(getMessage(code, locale)).build();
    }

    /**
     * building ApiError object to respond api endpoint
     *
     * @param exception
     * @param locale
     * @return
     */
    public ApiError apiError(MethodArgumentNotValidException exception, Locale locale) {
        List<ErrorDetails> errorDetails = populateErrorDetails(exception, locale);
        return ApiError.builder().code(ErrorCode.BAD_REQUEST.code()).message(getMessage(ErrorCode.BAD_REQUEST, locale))
                .details(errorDetails).build();
    }

    /**
     * to validate is error code is empty or not
     * if empty/null then return empty
     * else return actual error code
     *
     * @param code
     * @return
     */
    private String getActualErrorCode(ErrorCode code) {
        return Objects.nonNull(code) ? code.code() : StringConstants.EMPTY;
    }

    /**
     * get error code from enum
     *
     * @param code
     * @return
     */
    private ErrorCode getErrorCode(String code) {
        try {
            return ErrorCode.valueOf(code);
        } catch (Exception e) {
            log.error(NO_ERROR_CODE_FOUND_FOR, code);
        }
        return null;
    }

    /**
     * get message for error code
     *
     * @param code
     * @param locale
     * @return
     */
    private String getMessage(ErrorCode code, Locale locale) {
        String message = StringConstants.EMPTY;
        if (Objects.nonNull(code)) {
            try {
                message = messageSource.getMessage(getMessageKey(code.code()), null, locale);
            } catch (NoSuchMessageException exp) {
                log.error(NO_MESSAGE_FOUND_FOR, code.code());
            }
        }
        return message;
    }

    /**
     * helper to get message from message property file
     *
     * @param code
     * @param exception
     * @param locale
     * @return
     */
    private String getMessage(ErrorCode code, BusinessException exception, Locale locale) {
        String message = StringConstants.EMPTY;
        if (Objects.nonNull(code)) {
            try {
                message = exception.getMessage();
                if (StringUtils.isNotBlank(message) && message.startsWith(CODE_DEFAULT_CODE)) {
                    message = StringUtils.substringAfter(message, CODE_DEFAULT_CODE);
                } else {
                    message = messageSource.getMessage(getMessageKey(code.code()), null, locale);
                }
            } catch (NoSuchMessageException exp) {
                log.error(NO_MESSAGE_FOUND_FOR, code.code());
            }
        }
        return message;
    }

    /**
     * preparing error code
     *
     * @param code
     * @return
     */
    private String getMessageKey(String code) {
        return ERROR_KEY_FORMAT + code;
    }

    /**
     * to get faild error code messages
     *
     * @param fieldError
     * @return
     */
    private String getValue(FieldError fieldError) {
        Object value = fieldError.getRejectedValue();
        return Objects.nonNull(value) ? fieldError.getRejectedValue().toString() : null;
    }

    /**
     * to populate and build error response to endpoint
     *
     * @param fieldError
     * @param locale
     * @return
     */
    private ErrorDetails populateErrorDetail(FieldError fieldError, Locale locale) {
        ErrorCode errorCode = getErrorCode(fieldError.getDefaultMessage());
        return ErrorDetails.builder().code(getActualErrorCode(errorCode)).message(getMessage(errorCode, locale))
                .attribute(ErrorAttribute.builder().key(fieldError.getField()).value(getValue(fieldError)).build())
                .build();
    }

    /**
     * helper to populate and build error response to endpoint
     *
     * @param exception
     * @param locale
     * @return
     */
    private List<ErrorDetails> populateErrorDetails(MethodArgumentNotValidException exception, Locale locale) {
        return exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> populateErrorDetail(fieldError, locale)).collect(Collectors.toList());

    }

}
