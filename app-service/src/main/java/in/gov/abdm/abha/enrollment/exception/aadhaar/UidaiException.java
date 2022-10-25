package in.gov.abdm.abha.enrollment.exception.aadhaar;

import in.gov.abdm.abha.enrollment.exception.aadhaar.helper.UidaiErrorHelper;
import in.gov.abdm.abha.enrollment.exception.aadhaar.model.ErrorCode;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarResponseDto;

/**
 * parent handler for uidai exceptions
 */
public class UidaiException extends BusinessException {
	/**
	 * constant for error codes
	 */
	private static final ErrorCode CODE = ErrorCode.UIDAI_ERROR;
	/**
	 * constant for logging
	 */
	private static final String DEFAULT_MESSAGE = "Exception occurred while calling the UIDAI service";

	/**
	 * serial version of uid
	 */
	private static final long serialVersionUID = -2326655836303821529L;

	/**
	 * throwing exception to end points
	 * @param dOAadhaarResponseDto
	 */
	public UidaiException(AadhaarResponseDto dOAadhaarResponseDto) {
		super(UidaiErrorHelper.errorCode(dOAadhaarResponseDto, CODE), UidaiErrorHelper.errorMessage(dOAadhaarResponseDto, DEFAULT_MESSAGE));
	}

	/**
	 * throwing exception to endpoint with message
	 * @param message
	 */
	public UidaiException(String message) {
		super(CODE, message);
	}

}
