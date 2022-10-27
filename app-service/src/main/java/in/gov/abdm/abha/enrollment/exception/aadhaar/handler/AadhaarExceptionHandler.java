package in.gov.abdm.abha.enrollment.exception.aadhaar.handler;

import in.gov.abdm.abha.enrollment.exception.aadhaar.BusinessException;
import in.gov.abdm.abha.enrollment.exception.aadhaar.helper.ExceptionHelper;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class AadhaarExceptionHandler {

	/**
	 * constants for logging
	 */
	private static final String EXCEPTION_MSG_FORMAT = "{} Execution occurred while processing the request. Exception : ";
	public static final String BUSINESS = "Business";
	public static final String RESPONSE_BLOCK = "{}";

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
}
