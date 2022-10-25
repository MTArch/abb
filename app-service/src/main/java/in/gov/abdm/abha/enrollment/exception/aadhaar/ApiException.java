package in.gov.abdm.abha.enrollment.exception.aadhaar;

import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ErrorCode;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Exception to UIDAI response
 */
public class ApiException extends RuntimeException {

	/**
	 * serial version of UID
	 */
	private static final long serialVersionUID = -8262500809779681557L;
	/**
	 * constants for logging
	 */
	private static final String API_EXP_FORMAT = "%s: Code: [%s]-[%s], Message: %s ";
	public static final String TRACE = ", Trace : ";

	private boolean enableTrace = false;

	/**
	 * enum for UIDAI errors
	 */
	@Getter
	private final ErrorCode code;

	/**
	 * assigning error code to local error code
	 * @param code
	 */
	public ApiException(ErrorCode code) {
		this.code = code;
	}

	/**
	 * assigning error code and message to local error code and message
	 * @param code
	 * @param message
	 */
	public ApiException(ErrorCode code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * assigning error code and message to local error code and message
	 * @param code
	 * @param message
	 * @param cause
	 */
	public ApiException(ErrorCode code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.enableTrace = true;
	}

	/**
	 * assigning error code to local error code
	 * @param code
	 * @param cause
	 */
	public ApiException(ErrorCode code, Throwable cause) {
		super(cause);
		this.code = code;
		this.enableTrace = true;
	}

	/**
	 * to string for error codes and messages
	 * @return
	 */
	@Override
	public String toString() {
		String trace = String.format(API_EXP_FORMAT, this.getClass().getSimpleName(), getCode().name(),
				getCode().code(), getMessage());
		if (enableTrace) {
			trace = trace.concat(TRACE).concat(ExceptionUtils.getStackTrace(getCause()));
		}
		return trace;
	}

}
