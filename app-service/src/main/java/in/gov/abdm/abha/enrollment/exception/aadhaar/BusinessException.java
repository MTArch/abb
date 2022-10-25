package in.gov.abdm.abha.enrollment.exception.aadhaar;

import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ErrorAttribute;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ErrorCode;
import lombok.Getter;

/**
 * UIDAI Exceptions to respond endpoints
 */
public class BusinessException extends ApiException {

	/**
	 * UIDAI verions uid
	 */
	private static final long serialVersionUID = 4151569448693017560L;

	/**
	 * error attribute value
	 */
	@Getter
	private ErrorAttribute attribute;

	/**
	 * assigning error code to local error code
	 * @param code
	 */
	public BusinessException(ErrorCode code) {
		super(code);
	}

	/**
	 * assigning error code to local error code
	 * @param code
	 */
	public BusinessException(ErrorCode code, ErrorAttribute attribute) {
		super(code);
		this.attribute = attribute;
	}

	/**
	 * assigning error code to local error code
	 * @param code
	 */
	public BusinessException(ErrorCode code, ErrorAttribute attribute, Throwable cause) {
		super(code, cause);
		this.attribute = attribute;
	}

	/**
	 * assigning error code to local error code and message
	 * @param code
	 */
	public BusinessException(ErrorCode code, String message) {
		super(code, message);
	}

	/**
	 * assigning error code to local error code and message
	 * @param code
	 */
	public BusinessException(ErrorCode code, String message, ErrorAttribute attribute) {
		super(code, message);
		this.attribute = attribute;
	}

	/**
	 * assigning error code to local error code and message and error attribute
	 * @param code
	 */
	public BusinessException(ErrorCode code, String message, ErrorAttribute attribute, Throwable cause) {
		super(code, message, cause);
		this.attribute = attribute;
	}

	/**
	 * assigning error code to local error code and message
	 * @param code
	 */
	public BusinessException(ErrorCode code, String message, Throwable cause) {
		super(code, message, cause);
	}

	/**
	 * assigning error code to local error code
	 * @param code
	 */
	public BusinessException(ErrorCode code, Throwable cause) {
		super(code, cause);
	}
}
