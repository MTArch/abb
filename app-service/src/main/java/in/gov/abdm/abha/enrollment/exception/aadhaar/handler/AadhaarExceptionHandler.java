package in.gov.abdm.abha.enrollment.exception.aadhaar.handler;

import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarOtpException;
import in.gov.abdm.abha.enrollment.exception.aadhaar.BusinessException;
import in.gov.abdm.abha.enrollment.exception.aadhaar.helper.ExceptionHelper;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class AadhaarExceptionHandler {

	/**
	 * constants for logging
	 */
	private static final String EXCEPTION_MSG_FORMAT = "{} Execution occurred while processing the request. Exception : ";
	public static final String BUSINESS = "Business";
	public static final String RESPONSE_BLOCK = "{}";

	private static final String EXCEPTIONS = "Exceptions : ";
	private static final String MESSAGE = "Message";
	private static final String RESPONSE_TIMESTAMP = "timestamp";

	private static final String STATUS = "status";
	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	/**
	 * exception responding helper
	 * it find-out the error codes and message for those error codes
	 */
	@Autowired
	private ExceptionHelper helper;

	/**
	 * to handle Aadhaar service exceptions
	 * and response with proper error message to client application
	 * @param busExp
	 * @param request
	 * @return
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Mono<ApiError>> handleBusinessException(BusinessException busExp, ServerHttpRequest request) {
		log.error(EXCEPTION_MSG_FORMAT + RESPONSE_BLOCK, BUSINESS, busExp.getMessage());
		return ResponseEntity.unprocessableEntity().body(Mono.just(helper.apiError(busExp, LocaleContextHolder.getLocale())));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(AadhaarOtpException.class)
	public Map<String, Object> handleAadhaarOtpException(AadhaarOtpException ex) {
		Map<String, Object> errorMap = new LinkedHashMap<>();
		errorMap.put(MESSAGE, ex.getMessage());
		log.info(EXCEPTIONS, ex.getMessage());
		errorMap.put(STATUS, HttpStatus.BAD_REQUEST);
		errorMap.put(RESPONSE_TIMESTAMP, LocalDateTime.now().format(dateTimeFormatter));
		return errorMap;
	}
}
